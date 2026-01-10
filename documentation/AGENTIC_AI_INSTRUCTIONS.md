# Agentic AI Instructions - Atomic Analyst Project

**Purpose:** Guidelines for agentic AI development and implementation of Atomic Analyst Android application.

**Created:** January 10, 2026  
**Status:** ACTIVE - Implementation Phase  
**Modified By:** Agentic AI Assistant

---

## Rule 1: Documentation Management

### Three Documentation Files - Single Source of Truth
**ONLY these 3 files are documentation:**
1. ✅ **IMPLEMENTATION_ROADMAP.md** - Main vision and implementation plan
2. ✅ **prompt.md** - Original application requirements
3. ✅ **AGENTIC_AI_INSTRUCTIONS.md** - This file (AI behavioral guidelines)

**❌ NO OTHER DOCUMENTATION FILES** should be created unless explicitly requested by user

### Documentation Status
- **IMPLEMENTATION_ROADMAP.md** - Status: **MODIFIABLE** (until finalized by user)
  - Contains: 14 phases, technology stack, timeline, risks
  - Updates allowed: For corrections, clarifications, or scope adjustments
  - Lock condition: Will be set to IMMUTABLE only after user confirmation
  - Exception: Can be modified in rare cases (feature not implementable, new features needed)

### Operational Files (NOT Documentation)
The following are **NOT documentation files** - they are operational tracking files:
- **IMPLEMENTATION_CHECKLIST.md** - Progress tracking only (auto-updated as features implemented)

**Additional clarification:**
- Documentation files contain: vision, requirements, guidelines, instructions
- Operational files contain: progress status, checklists, completed features tracking
- Source code files are NOT documentation files
- No other files should be created without explicit user request

---

## Rule 2: Modern & Standard Coding Practices

### Language & Framework Standards
- **Language:** Kotlin 2.0+
- **Architecture:** MVVM + Clean Architecture
- **UI Framework:** Jetpack Compose (not XML layouts)
- **Database:** Room with SQLCipher
- **Dependency Injection:** Hilt
- **Async:** Coroutines (not RxJava)

### Code Style Requirements
1. **Naming Conventions**
   - Classes: PascalCase
   - Functions/Variables: camelCase
   - Constants: UPPER_SNAKE_CASE
   - Private members: prefix with underscore (optional, use sparingly)

2. **Package Structure** (Follow Clean Architecture)
   ```
   com.atomicanalyst/
   ├── di/              (Hilt modules)
   ├── data/            (Repositories, DAOs, API clients)
   ├── domain/          (Use cases, models)
   ├── presentation/    (UI, ViewModels)
   └── utils/           (Helpers, extensions)
   ```

3. **Best Practices**
   - Use `val` instead of `var` by default
   - Null safety: Use non-null types and `?.` operator appropriately
   - No hardcoded strings: Use strings.xml for UI text
   - Use `const val` for compile-time constants
   - Leverage Kotlin extensions and scope functions
   - Follow existing code patterns in the project

4. **Code Organization**
   - Keep functions small and focused
   - Maximum function length: 30 lines (prefer shorter)
   - Maximum class length: 200 lines (prefer shorter, split if needed)
   - Use sealed classes for type-safe hierarchies
   - Prefer data classes for value objects

---

## Rule 3: Test File Creation

### When to Create Test Files
- ✅ After every new Kotlin/Java file is created
- ✅ For all business logic classes (repositories, use cases, ViewModels)
- ✅ For data models and utilities
- ❌ NOT required for simple data classes without logic

### Test File Organization
```
src/test/kotlin/com/atomicanalyst/
└── [same package structure as main]

src/androidTest/kotlin/com/atomicanalyst/
└── [UI and integration tests]
```

### Test Naming Convention
- Test class: `[ClassName]Test` (e.g., `TransactionRepositoryTest`)
- Test function: `test[Scenario]_[Action]_[Expected]` (e.g., `testInsertTransaction_WithValidData_ReturnsSuccess`)

### Test Coverage Requirements
- Minimum: 60% for business logic
- Target: 70%+ overall code coverage
- All public methods tested
- Edge cases and error scenarios covered

### Test Framework
- **Unit Tests:** JUnit 4, Mockito
- **Instrumentation Tests:** Espresso, Compose UI tests
- **Test Data:** Use factory patterns for test data creation

---

## Rule 4: Build Verification & Management

### Build Process
1. **After Code Changes**
   - Run: `./gradlew build` or `./gradlew assembleDebug`
   - Wait for build to complete
   - Check for errors and warnings

2. **Build Success Criteria**
   - ✅ No compilation errors
   - ✅ All tests pass (if tests exist)
   - ✅ No critical lint warnings
   - ✅ APK/Bundle generated successfully

3. **Build Failure Handling**
   - **First Attempt:** Analyze and fix the error
   - **Second Attempt:** If related to the code change, fix and retry
   - **Third Attempt:** If still failing after multiple fixes, inform user
   - **User Notification:** "Build has failed after 3+ attempts. We may need to review the direction."

4. **Build Artifacts**
   - Debug APK: `app/build/outputs/apk/debug/`
   - Release APK: `app/build/outputs/apk/release/`
   - Bundle: `app/build/outputs/bundle/release/`

---

## Rule 5: Library Compatibility & Version Management

### Version Verification Strategy
1. **Before Using a Library**
   - Check version in `build.gradle.kts`
   - Verify Kotlin compatibility
   - Check Android API level requirements
   - Verify no conflicts with existing dependencies

2. **Library Compatibility Matrix**
   - Kotlin: 2.0+
   - Android API: 28-35
   - Gradle: 8.1.0+
   - AGP: 8.1.0+
   - Compose: 1.6.0+

3. **Strict Version Policy**
   - Stick to versions in IMPLEMENTATION_ROADMAP.md
   - Only use exact versions specified
   - No major version upgrades without user confirmation

4. **Version Upgrade Process**
   - ⚠️ **CRITICAL:** Get user confirmation before upgrading library versions
   - Document reason for upgrade
   - Test thoroughly after upgrade
   - Update IMPLEMENTATION_ROADMAP.md with new version
   - Create test cases for new version compatibility

### Current Approved Versions (from IMPLEMENTATION_ROADMAP.md)
Reference the dependencies section in IMPLEMENTATION_ROADMAP.md Phase 0 for all approved versions.

---

## Rule 6: Critical Change Notification & User Confirmation

### Changes Requiring User Confirmation
❌ **DO NOT PROCEED** without explicit user permission for:

1. **Large Scale Operations**
   - Delete multiple files (> 3 files)
   - Rename core packages
   - Large scale database schema changes

2. **Git Operations**
   - Git commits
   - Git pushes
   - Branch merges
   - Force push or resets

3. **CRUD Operations on Database**
   - Alter existing database schema
   - Migration of existing data
   - Deletion of database entities
   - Changes to Room database relationships

4. **Version Level Changes**
   - Android API level changes (minSdk, targetSdk)
   - Kotlin language version upgrades
   - Java version upgrades
   - Major library version migrations

5. **Major Code Refactoring**
   - Architectural changes
   - Module structure changes
   - Complete rewrite of components

6. **Breaking Changes**
   - Changing public API signatures
   - Removing user-facing features
   - Incompatible changes to data format

### Notification Format
```
⚠️  CRITICAL CHANGE ALERT

Action: [Describe the action]
Impact: [Describe the impact]
Confirmation: Please confirm before proceeding

User Response Required: YES/NO
```

---

## Rule 7: UI/UX Design Principles

### Simplicity & User-Friendliness
- ✅ Clean, minimal UI with clear visual hierarchy
- ✅ Intuitive navigation paths
- ✅ Consistent design patterns across screens
- ✅ Material 3 design system compliance
- ❌ No unnecessary buttons or options
- ❌ No cluttered screens

### User Experience Guidelines
1. **Navigation**
   - Maximum 2-3 taps to reach any feature
   - Clear back/cancel options
   - Consistent header layout

2. **Data Display**
   - Show most important information first
   - Use progressive disclosure for advanced options
   - Group related items together
   - Clear labels and hints

3. **Input Forms**
   - Minimize required fields
   - Pre-fill when possible
   - Clear validation messages
   - Keyboard handling on mobile

4. **Feedback**
   - Toast messages for quick feedback
   - Snackbars for undo actions
   - Loading indicators for async operations
   - Error messages in plain language

5. **Accessibility**
   - Minimum touch target: 48dp
   - Color contrast ratio: 4.5:1 for text
   - Content descriptions for images
   - Keyboard navigation support

---

## Rule 8: Performance Optimization

### Performance Priorities (Ranked)
1. **Critical:** App startup time < 2 seconds
2. **Critical:** List scrolling smooth (60 FPS)
3. **High:** Database queries < 100ms
4. **High:** API calls < 2 seconds
5. **Medium:** Memory usage < 150MB for normal operations
6. **Medium:** Battery impact minimal

### Performance Guidelines
- ✅ Use lazy loading for large lists
- ✅ Implement database indexing
- ✅ Cache API responses appropriately
- ✅ Use coroutines instead of blocking operations
- ✅ Profile memory usage regularly
- ❌ Don't load all data at once
- ❌ Avoid blocking main thread
- ❌ Don't perform network operations on main thread

### Monitoring
- Check Logcat for ANR (Application Not Responding) warnings
- Use Android Profiler for memory/CPU analysis
- Monitor battery drain with Battery Historian
- Test with different device configurations

---

## Rule 9: Library vs Custom Implementation Decision

### Decision Matrix

**Use Library IF:**
- ✅ Well-maintained and proven in production
- ✅ Good documentation and community support
- ✅ Minimal external dependencies
- ✅ Active development/security updates
- ✅ Significant time saving (> 20% of feature time)
- ✅ Tested by many projects with similar requirements

**Build Custom Code IF:**
- ✅ Library has compatibility issues
- ✅ Library is abandoned or poorly maintained
- ✅ Significant performance overhead
- ✅ Feature is simple enough to implement
- ✅ Need specific customization not offered
- ✅ Security concerns with library

### Examples from Project
**PDF Processing:** Use Apache PDFBox (well-maintained, proven)
**OCR:** Use Google ML Kit (official, lightweight, works offline)
**Encryption:** Use Android Security Crypto (official, vetted)
**Backup:** Consider custom implementation if Google Drive API has issues

### Escalation Process
1. Identify library issue/limitation
2. Evaluate custom implementation feasibility
3. If custom implementation viable: Implement and test
4. If neither viable: Inform user
   - "Library X has incompatibility with Y"
   - "Custom implementation would require Z effort"
   - "Recommendation: [Alternative approach or scope reduction]"
5. Wait for user decision before proceeding

---

## Rule 10: Implementation Checklist Management

### Checklist File
**Location:** `documentation/IMPLEMENTATION_CHECKLIST.md`  
**Purpose:** Track all implemented features and changes  
**Update Frequency:** After every completed feature or significant change

### Checklist Format
```markdown
## [Phase Name]
- [x] Completed feature 1
- [ ] Pending feature 2
- [!] In Progress feature 3

## Changes Log
- [Date] Feature Name - Status
- [Date] Bug Fix - Status
```

### What Goes in Checklist
✅ Feature implementations
✅ Bug fixes
✅ Test coverage additions
✅ Library upgrades
✅ Database migrations
✅ UI screen implementations
✅ API integrations
✅ Code refactoring

### What Doesn't Go in Checklist
❌ Minor code formatting
❌ Comment additions
❌ Lint warning fixes
❌ Build configuration tweaks (unless breaking)

---

## Rule 11: IMPLEMENTATION_ROADMAP.md - Modification Policy

### Current Status: **MODIFIABLE**
- ✅ Corrections and clarifications allowed
- ✅ Scope adjustments allowed
- ✅ Technology choices can be refined
- ✅ Timeline estimates can be adjusted based on actual implementation

### When Can It Be Modified?
1. **Corrections:** Fix errors in specification or timeline
2. **Clarifications:** Add details to existing phases
3. **Scope Changes:** Adjust feature scope if needed
4. **Technology Updates:** Change library versions or tools
5. **Exceptional Cases:**
   - Feature proves not implementable
   - New critical feature needs to be added
   - Major blockers discovered

### When Will It Be Locked?
- ✅ Only after user confirmation: "ROADMAP FINALIZED"
- At that point, it becomes IMMUTABLE
- No changes allowed except in exceptional cases (with user permission)

### How to Handle Requests for Changes
1. Explain the change with reasoning
2. Document impact on timeline
3. Update the IMPLEMENTATION_ROADMAP.md
4. Note the change in IMPLEMENTATION_CHECKLIST.md
5. Inform user of the modification

---

## Rule 12: Three Documentation Files System

### The Three Documentation Files (ONLY)
| File | Purpose | Status | Location |
|------|---------|--------|----------|
| **IMPLEMENTATION_ROADMAP.md** | Vision & Implementation Plan | Modifiable | documentation/ |
| **prompt.md** | Original Requirements | Immutable | documentation/ |
| **AGENTIC_AI_INSTRUCTIONS.md** | AI Behavioral Guidelines | Active | documentation/ |

**These are the ONLY documentation files allowed. No exceptions unless explicitly requested by user.**

### Operational Tracking Files (NOT Documentation)
| File | Purpose | Status | Location |
|------|---------|--------|----------|
| **IMPLEMENTATION_CHECKLIST.md** | Progress tracking only | Auto-updated | documentation/ |

**Important Distinction:**
- **Documentation files** = contain vision, requirements, and instructions
- **Operational files** = track progress and status only
- **IMPLEMENTATION_CHECKLIST.md is NOT a documentation file**

### No Additional Files Unless
- ❌ User explicitly requests new documentation
- ❌ User provides specific requirements for new documentation
- ❌ Project operational needs (e.g., source code files, build files)

---

## Rule 13: Instructions File - This Document

### Purpose
- Define behavioral guidelines for agentic AI
- Establish coding standards and practices
- Document decision-making processes
- Ensure consistency across implementation

### Authority
- These instructions are BINDING for AI decision-making
- Override conflicting guidelines from other sources
- Can be updated only by user request
- Current version supersedes all previous guidelines

### Updates to This Document
- User can request modifications anytime
- Changes should be documented with date and reason
- Communicate changes to AI at start of session
- Keep version history (date-based)

### Version History
- **v1.0** - January 10, 2026 - Initial creation with 13 rules

---

## Quick Reference - Rule Summary

| Rule | Key Point |
|------|-----------|
| 1 | **Only 3 documentation files:** ROADMAP, Prompt, Instructions (checklist is operational, not documentation) |
| 2 | Use Kotlin 2.0+, MVVM, Clean Architecture, Jetpack Compose |
| 3 | Create test files for every new implementation |
| 4 | Build after each change, fix fails immediately |
| 5 | Strict version management, user confirms upgrades |
| 6 | Get user permission for critical changes |
| 7 | Keep UI simple, Material 3 compliant |
| 8 | Performance is priority, < 2sec startup, 60 FPS scrolling |
| 9 | Use proven libraries, build custom only when necessary |
| 10 | Maintain IMPLEMENTATION_CHECKLIST.md (operational file) of all changes |
| 11 | ROADMAP is modifiable until user finalizes it |
| 12 | Stick to 3-documentation-file system (+ 1 operational checklist) |
| 13 | These instructions are binding for AI behavior |

---

## Implementation Workflow

### For Every Feature Implementation
```
1. Review IMPLEMENTATION_ROADMAP.md for specifications
2. Check IMPLEMENTATION_CHECKLIST.md for dependencies
3. Create/update code following Rule 2 (coding practices)
4. Create test files (Rule 3)
5. Run build (Rule 4)
6. If build fails: Fix and retry (Rule 4)
7. Update IMPLEMENTATION_CHECKLIST.md (Rule 10)
8. For critical changes: Notify user (Rule 6)
9. For library issues: Decide library vs custom (Rule 9)
```

### For Every Critical Decision
```
1. Check IMPLEMENTATION_ROADMAP.md for guidance
2. Evaluate against project requirements
3. Consider performance impact (Rule 8)
4. Consider compatibility (Rule 5)
5. If uncertain: Ask user for confirmation (Rule 6)
6. Document decision in code comments
7. Update IMPLEMENTATION_CHECKLIST.md
```

---

## Communication Guidelines

### To User - Status Updates
- Report completed features
- Notify of blockers or issues
- Request confirmations when needed
- Provide ETA for major features
- Explain technical decisions when asked

### To User - Problem Reporting
- Clear description of issue
- Impact on timeline
- Options for resolution
- Recommendation
- Request for user decision

### Format for Critical Alerts
```
🔴 CRITICAL ISSUE
[Issue Description]
[Impact]
[Options]
User confirmation required: [YES/NO]
```

---

## Final Notes

- These instructions ensure consistency and quality throughout Atomic Analyst development
- Follow these guidelines strictly; they represent best practices for this project
- Escalate to user any situations not covered by these rules
- Keep user informed of progress and blockers
- Focus on delivering production-quality code with comprehensive testing
- Performance and user experience are top priorities

**Last Updated:** January 10, 2026  
**Effective From:** Start of implementation phase  
**Review Cycle:** On user request or after major milestones

