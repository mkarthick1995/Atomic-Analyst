# Atomic Analyst

**An Android application for comprehensive personal financial analysis and insights.**

## Vision

Atomic Analyst monitors a user's finances at a molecular level. It captures every expenditure channel—GPay, PhonePe, UPI, credit/debit cards, wallets, and cash—and turns those transactions into actionable insights that improve long-term financial health.

## Key Features

- **Comprehensive Transaction Ingestion** - Aggregate data from all payment sources (live feeds, PDFs, receipts)
- **Smart Categorization** - AI-powered automatic expense classification with user learning
- **Advanced Reporting** - Daily, weekly, monthly, and annual reports with visualizations
- **AI Assistance** - OpenAI integration for personalized insights and recommendations
- **Multi-Account Support** - Track multiple bank accounts, credit cards, wallets, and loans
- **De-duplication & Reconciliation** - Eliminate duplicate transactions, detect internal transfers
- **Anomaly Detection** - Get alerted to unusual spending patterns
- **Expense Forecasting** - Predict future spending using historical trends
- **Budget Management** - Set category-based budgets with threshold alerts

## Technology Stack

- **Language:** Kotlin 2.0+
- **Architecture:** MVVM + Clean Architecture
- **UI:** Jetpack Compose with Material 3
- **Database:** Room with SQLCipher (encrypted)
- **Dependency Injection:** Hilt
- **Async:** Coroutines
- **APIs:** Retrofit, OpenAI
- **Testing:** JUnit, Mockito, Espresso

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/atomicanalyst/
│   │   │   ├── di/              (Dependency Injection)
│   │   │   ├── data/            (Data Layer)
│   │   │   ├── domain/          (Domain Layer)
│   │   │   ├── presentation/    (UI Layer)
│   │   │   └── utils/           (Utilities)
│   │   └── res/
│   ├── test/
│   └── androidTest/
├── build.gradle.kts
└── proguard-rules.pro
```

## Documentation

- **[IMPLEMENTATION_ROADMAP.md](documentation/IMPLEMENTATION_ROADMAP.md)** - Complete 14-phase implementation plan
- **[AGENTIC_AI_INSTRUCTIONS.md](documentation/AGENTIC_AI_INSTRUCTIONS.md)** - AI behavioral guidelines (13 rules)
- **[IMPLEMENTATION_CHECKLIST.md](documentation/IMPLEMENTATION_CHECKLIST.md)** - Phase-wise progress tracking

## Implementation Phases

The project is organized into 14 phases:

1. **Phase 0** - Project Setup & Infrastructure
2. **Phase 1** - Core Architecture & Foundation
3. **Phase 2** - Authentication & Data Security (with backup system)
4. **Phase 3** - Transaction Data Layer
5. **Phase 4** - Account Management
6. **Phase 5** - Transaction Ingestion & Parsing
7. **Phase 6** - Categorization Engine
8. **Phase 7** - De-duplication & Reconciliation
9. **Phase 8** - Analytics & Reporting
10. **Phase 9** - AI Integration (OpenAI)
11. **Phase 10** - Notifications & Alerts
12. **Phase 11** - Forecasting Module
13. **Phase 12** - UI/UX & User Experience
14. **Phase 13** - Testing & Quality Assurance
15. **Phase 14** - Release & Deployment

**Estimated Timeline:** 37-51 weeks (9-12 months) with 2-3 senior Android developers

## Getting Started

### Prerequisites
- Android Studio (Latest)
- Kotlin 2.0+
- JDK 11+
- Gradle 8.1.0+

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/mkarthick1995/Atomic-Analyst.git
cd Atomic-Analyst

# Build the project
./gradlew build

# Run tests
./gradlew test

# Build debug APK
./gradlew assembleDebug
```

## Project Status

**Current Phase:** Planning & Setup (Phase 0)  
**Overall Progress:** 0%  
**Last Updated:** January 10, 2026

### Completed
- ✅ Documentation system established
- ✅ AI behavioral guidelines created
- ✅ Project structure defined
- ✅ Technology stack finalized
- ✅ Git repository initialized
- ✅ Implementation roadmap created

### Next Steps
- ⏳ Phase 0: Project Setup & Infrastructure
- ⏳ Initialize Android project
- ⏳ Configure build system
- ⏳ Begin Phase 1 implementation

## Architecture

The project follows **MVVM + Clean Architecture** pattern:

```
Presentation Layer (UI)
    ↓
ViewModel Layer
    ↓
Domain Layer (Use Cases)
    ↓
Data Layer (Repository)
    ↓
Data Sources (DB, API, Files)
```

Each layer is independent and testable, with proper dependency injection via Hilt.

## Critical Requirements

### Data Security & Backup (Phase 2 - MANDATORY)
- ✅ 3-tier backup system designed
- ✅ Automatic cloud backup (daily @ 2 AM)
- ✅ Manual local backup export
- ✅ Incremental sync (future)
- **Status:** CRITICAL - Must implement before release

### Performance Targets
- App startup: < 2 seconds
- List scrolling: 60 FPS
- Database queries: < 100ms
- Memory usage: < 150MB

## Contributing

This is an internal project. Contributions follow the AGENTIC_AI_INSTRUCTIONS.md guidelines.

### Code Standards
- Kotlin 2.0+ with null safety
- MVVM + Clean Architecture
- Jetpack Compose for UI
- Comprehensive test coverage (70%+)
- Modern Android best practices

### Testing Requirements
- Unit tests for business logic (min 60% coverage)
- Integration tests for data layer
- UI tests for screens
- Build verification after changes

## License

Proprietary - Internal Project

## Contact

- **Project Lead:** mkarthick1995
- **Repository:** https://github.com/mkarthick1995/Atomic-Analyst

## Important Notes

- All work follows the AGENTIC_AI_INSTRUCTIONS.md
- Single documentation source of truth: IMPLEMENTATION_ROADMAP.md
- Project directory: `\\wsl.localhost\Ubuntu\home\mkarthick1995\Projects\Atomic Analyst`
- Latest Android standards (API 28-35)

---

**Created:** January 10, 2026  
**Status:** ✅ Ready for Phase 0 Implementation

