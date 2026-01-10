# Atomic Analyst Prompt (Rev 2)

## Vision
Atomic Analyst is an Android application that monitors a user's finances at a molecular level. It captures every expenditure channel—GPay, PhonePe, UPI, credit/debit cards, wallets, and cash—and turns those transactions into actionable insights that improve long-term financial health.

## Core Requirements
1. **Comprehensive ingestion:** Aggregate and normalize data from all supported payment sources—live feeds and uploaded statements (PDFs/images)—with robust parsing.
2. **Categorization engine:** Auto-classify expenses, allow user overrides, and learn from corrections.
3. **Reporting suite:** Generate configurable reports (daily/weekly/monthly/annual) tailored to user preferences and export formats.
4. **AI assistance:** Utilize OpenAI APIs for personalized summaries, recommendations, anomaly detection, and conversation-based analytics.
5. **Accounts & balances:** Model multiple accounts (banks, wallets, credit cards, loans) with context such as tied liabilities, credit limits, and standing instructions.
6. **Statement & receipt processing:** Ingest bank/wallet PDFs, invoices, or photographed receipts (e.g., supermarket bills), extract line items with OCR, and reconcile against live data.
7. **Transaction taxonomy:** Support expenses, incomes, loans, EMIs, and internal transfers; allow tagging internal moves (e.g., SBI → HDFC) so they appear in history without distorting budgets.
8. **De-duplication logic:** Detect and collapse mirrored transactions (e.g., credit card purchase vs. bill payment) and intra-account transfers while preserving audit trails.
9. **Notifications & limits:** Alert users when approaching or exceeding custom spending thresholds; surface root causes.
10. **Forecasting:** Predict next day/month/year expenses using historical trends and behavioral signals.
11. **Guidance:** Recommend ways to reduce unnecessary spend, identify cancellable subscriptions, and highlight healthy investment opportunities.

## Tone & Style
- Be supportive, pragmatic, and user-focused; keep instructions concise.
- Explain financial AI decisions in plain language, noting confidence and trade-offs.
- Emphasize privacy, data security, and responsible AI usage whenever relevant.

## Delivery Checklist
- ✅ Confirm data sources included in each flow and note gaps.
- ✅ Provide testing or validation steps for financial calculations and AI outputs.
- ✅ Document limitations, dependencies (e.g., API keys), and follow-up actions.

## Future Considerations
- Expand to proactive budgeting/coaching modules.
- Introduce collaborative features for families or small teams.
- Offer plugin architecture for banks, wallets, and tax tools.
