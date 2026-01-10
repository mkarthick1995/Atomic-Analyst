#!/usr/bin/env node
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { Octokit } from 'octokit';
import * as z from 'zod';

type TextContent = { type: 'text'; text: string };

const toTextContent = (payload: unknown): TextContent[] => [
  {
    type: 'text',
    text: typeof payload === 'string' ? payload : JSON.stringify(payload, null, 2),
  },
];

const formatError = (error: unknown) => ({
  content: toTextContent(error instanceof Error ? error.message : String(error)),
  isError: true,
});

const createOctokit = () => {
  const token = process.env.GITHUB_TOKEN;

  if (!token) {
    throw new Error('GITHUB_TOKEN is not set. Provide a GitHub token with repo access.');
  }

  return new Octokit({ auth: token });
};

const octokit = createOctokit();

const server = new McpServer(
  { name: 'github-mcp', version: '0.1.0' },
  {
    capabilities: { tools: {} },
    instructions:
      'GitHub helper tools: list repositories, read/write files, create branches, and open pull requests.',
  },
);

server.registerTool(
  'list_repos',
  {
    description: 'List repositories for the authenticated user (owner, collaborator, and org memberships).',
    outputSchema: {
      repositories: z.array(
        z.object({
          fullName: z.string(),
          private: z.boolean(),
          defaultBranch: z.string().nullable(),
        }),
      ),
    },
  },
  async () => {
    try {
      const repos = await octokit.paginate(octokit.rest.repos.listForAuthenticatedUser, {
        per_page: 100,
        affiliation: 'owner,collaborator,organization_member',
      });

      const simplified = repos.map((repo) => ({
        fullName: repo.full_name,
        private: repo.private ?? false,
        defaultBranch: repo.default_branch ?? null,
      }));

      const structuredContent = { repositories: simplified };
      return {
        content: toTextContent(structuredContent),
        structuredContent,
      };
    } catch (error) {
      return formatError(error);
    }
  },
);

server.registerTool(
  'get_file',
  {
    description: 'Read a single file from a repository.',
    inputSchema: {
      owner: z.string().describe('Repository owner or organization'),
      repo: z.string().describe('Repository name'),
      path: z.string().describe('Path to the file'),
      ref: z.string().describe('Branch, tag, or commit SHA').optional(),
    },
    outputSchema: {
      path: z.string(),
      sha: z.string(),
      size: z.number(),
      content: z.string(),
      encoding: z.string(),
      downloadUrl: z.string().url().nullable(),
      htmlUrl: z.string().url().nullable(),
    },
  },
  async ({ owner, repo, path, ref }) => {
    try {
      const response = await octokit.rest.repos.getContent({ owner, repo, path, ref });
      const data = response.data;

      if (Array.isArray(data)) {
        throw new Error('The requested path is a directory. Provide a file path instead.');
      }

      if (data.type !== 'file' || !('content' in data)) {
        throw new Error('The requested path is not a file.');
      }

      const decoded = Buffer.from(data.content ?? '', data.encoding as BufferEncoding).toString('utf8');
      const structuredContent = {
        path: data.path,
        sha: data.sha,
        size: data.size,
        content: decoded,
        encoding: data.encoding,
        downloadUrl: 'download_url' in data ? data.download_url ?? null : null,
        htmlUrl: 'html_url' in data ? data.html_url ?? null : null,
      };

      return {
        content: toTextContent(structuredContent),
        structuredContent,
      };
    } catch (error) {
      return formatError(error);
    }
  },
);

server.registerTool(
  'put_file',
  {
    description:
      'Create or update a file with a commit message. Provide `sha` when updating an existing file.',
    inputSchema: {
      owner: z.string().describe('Repository owner or organization'),
      repo: z.string().describe('Repository name'),
      path: z.string().describe('Path to the file'),
      message: z.string().describe('Commit message'),
      content: z.string().describe('File contents (UTF-8)'),
      branch: z.string().describe('Branch name; defaults to the default branch').optional(),
      sha: z.string().describe('Existing file SHA (required for updates)').optional(),
    },
    outputSchema: {
      commitSha: z.string(),
      contentSha: z.string().nullable(),
      downloadUrl: z.string().url().nullable(),
      htmlUrl: z.string().url().nullable(),
    },
  },
  async ({ owner, repo, path, message, content, branch, sha }) => {
    try {
      const result = await octokit.rest.repos.createOrUpdateFileContents({
        owner,
        repo,
        path,
        message,
        branch,
        sha,
        content: Buffer.from(content, 'utf8').toString('base64'),
      });

      const structuredContent = {
        commitSha: result.data.commit.sha,
        contentSha: result.data.content?.sha ?? null,
        downloadUrl:
          'download_url' in (result.data.content ?? {}) ? result.data.content?.download_url ?? null : null,
        htmlUrl: 'html_url' in (result.data.content ?? {}) ? result.data.content?.html_url ?? null : null,
      };

      return {
        content: toTextContent(structuredContent),
        structuredContent,
      };
    } catch (error) {
      return formatError(error);
    }
  },
);

const normalizeBranch = (value: string) => value.replace(/^refs\/heads\//, '');

server.registerTool(
  'create_branch',
  {
    description: 'Create a new branch from an existing branch.',
    inputSchema: {
      owner: z.string().describe('Repository owner or organization'),
      repo: z.string().describe('Repository name'),
      fromRef: z.string().describe('Source branch name (or refs/heads/ prefixed)'),
      newBranch: z.string().describe('Name of the branch to create'),
    },
    outputSchema: {
      ref: z.string(),
      sha: z.string(),
    },
  },
  async ({ owner, repo, fromRef, newBranch }) => {
    try {
      const sourceBranch = normalizeBranch(fromRef);
      const targetBranch = normalizeBranch(newBranch);

      const base = await octokit.rest.git.getRef({
        owner,
        repo,
        ref: `heads/${sourceBranch}`,
      });

      const created = await octokit.rest.git.createRef({
        owner,
        repo,
        ref: `refs/heads/${targetBranch}`,
        sha: base.data.object.sha,
      });

      const structuredContent = {
        ref: created.data.ref,
        sha: created.data.object.sha,
      };

      return {
        content: toTextContent(structuredContent),
        structuredContent,
      };
    } catch (error) {
      return formatError(error);
    }
  },
);

server.registerTool(
  'create_pull_request',
  {
    description: 'Open a pull request.',
    inputSchema: {
      owner: z.string().describe('Repository owner or organization'),
      repo: z.string().describe('Repository name'),
      title: z.string().describe('Pull request title'),
      head: z.string().describe('Source branch (e.g., feature-branch)'),
      base: z.string().describe('Target branch (e.g., main)'),
      body: z.string().describe('Pull request body').optional(),
    },
    outputSchema: {
      number: z.number(),
      url: z.string().url(),
      state: z.string(),
      head: z.string(),
      base: z.string(),
    },
  },
  async ({ owner, repo, title, head, base, body }) => {
    try {
      const pr = await octokit.rest.pulls.create({
        owner,
        repo,
        title,
        head,
        base,
        body,
      });

      const structuredContent = {
        number: pr.data.number,
        url: pr.data.html_url,
        state: pr.data.state,
        head: pr.data.head.ref,
        base: pr.data.base.ref,
      };

      return {
        content: toTextContent(structuredContent),
        structuredContent,
      };
    } catch (error) {
      return formatError(error);
    }
  },
);

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error('GitHub MCP server is running over stdio.');
}

main().catch((error) => {
  console.error('Failed to start GitHub MCP server:', error);
  process.exit(1);
});
