# Atomic Analyst

An Android application for comprehensive personal financial analysis and insights.

## Vision

Atomic Analyst monitors a user's finances at a molecular level. It captures every expenditure channel (GPay, PhonePe, UPI, credit/debit cards, wallets, and cash) and turns those transactions into actionable insights that improve long-term financial health.

## Key Features

- Comprehensive transaction ingestion (live feeds, PDFs, receipts)
- Smart categorization with user learning
- Advanced reporting (daily, weekly, monthly, annual)
- AI assistance for insights and recommendations
- Multi-account support
- De-duplication and reconciliation
- Anomaly detection
- Expense forecasting
- Budget management and alerts

## Technology Stack

- Language: Kotlin 2.3.0+
- Architecture: MVVM + Clean Architecture
- UI: Jetpack Compose with Material 3
- Database: Room with SQLCipher (encrypted)
- Dependency Injection: Hilt
- Async: Coroutines
- APIs: Retrofit, OpenAI
- Testing: JUnit, Mockito, Espresso

## Project Structure

```
app/
|-- src/
|   |-- main/
|   |   |-- java/com/atomicanalyst/
|   |   |   |-- di/              (Dependency Injection)
|   |   |   |-- data/            (Data Layer)
|   |   |   |-- domain/          (Domain Layer)
|   |   |   |-- presentation/    (UI Layer)
|   |   |   `-- utils/           (Utilities)
|   |   `-- res/
|   |-- test/
|   `-- androidTest/
|-- build.gradle.kts
`-- proguard-rules.pro
```

## Documentation

- IMPLEMENTATION_ROADMAP.md: documentation/IMPLEMENTATION_ROADMAP.md
- AGENTIC_AI_INSTRUCTIONS.md: documentation/AGENTIC_AI_INSTRUCTIONS.md
- IMPLEMENTATION_CHECKLIST.md: documentation/IMPLEMENTATION_CHECKLIST.md

## Implementation Phases

1. Phase 0 - Project Setup and Infrastructure
2. Phase 1 - Core Architecture and Foundation
3. Phase 2 - Authentication and Data Security (with backup system)
4. Phase 3 - Transaction Data Layer
5. Phase 4 - Account Management
6. Phase 5 - Transaction Ingestion and Parsing
7. Phase 6 - Categorization Engine
8. Phase 7 - De-duplication and Reconciliation
9. Phase 8 - Analytics and Reporting
10. Phase 9 - AI Integration (OpenAI)
11. Phase 10 - Notifications and Alerts
12. Phase 11 - Forecasting Module
13. Phase 12 - UI/UX and User Experience
14. Phase 13 - Testing and Quality Assurance
15. Phase 14 - Release and Deployment

Estimated timeline: 37-51 weeks (9-12 months) with 2-3 senior Android developers

## Getting Started

### Prerequisites
- Android Studio (latest stable)
- Kotlin 2.3.0+
- JDK 17+ (21 recommended)
- Gradle 8.13.0+

### Build Instructions

Windows:
```bash
gradlew.bat build
gradlew.bat test
gradlew.bat assembleDebug
```

Linux/macOS:
```bash
./gradlew build
./gradlew test
./gradlew assembleDebug
```

## Project Status

Current Phase: Phase 2 - Authentication and Data Security (In Progress)
Overall Progress: 18%
Last Updated: February 7, 2026

### Completed
- Phase 0 and Phase 1 foundations
- Toolchain stabilized (Gradle 8.13, AGP 8.13.2, Kotlin 2.3.0, Hilt 2.58)
- Local authentication foundations (register/login/logout)
- Secure storage, session management, backup crypto scaffolding

### Next Steps
- Continue Phase 2: SQLCipher wired to Room (requires Phase 3 DB)
- Add certificate pinning with real host pins
- Cloud backup (Drive/OneDrive) and remaining security tests

## Architecture

The project follows MVVM + Clean Architecture:

```
Presentation Layer (UI)
    |
ViewModel Layer
    |
Domain Layer (Use Cases)
    |
Data Layer (Repository)
    |
Data Sources (DB, API, Files)
```

Each layer is independent and testable, with dependency injection via Hilt.

## Critical Requirements

### Data Security and Backup (Phase 2 - mandatory)
- Local encrypted backup and restore implemented
- Cloud backup (Drive/OneDrive) pending
- Status: In progress; must complete before release

### Performance Targets
- App startup: < 2 seconds
- List scrolling: 60 FPS
- Database queries: < 100ms
- Memory usage: < 150MB

## Contributing

Internal project. Follow AGENTIC_AI_INSTRUCTIONS.md.

### Code Standards
- Kotlin 2.3.0+ with null safety
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

- Project Lead: mkarthick1995
- Repository: https://github.com/mkarthick1995/Atomic-Analyst

## Important Notes

- Backend service is post-MVP and not required for current phases
- Single documentation source of truth: IMPLEMENTATION_ROADMAP.md
- Project directory: C:\Workspace\Atomic-Analyst
- Latest Android standards (API 28-35)

---

Created: January 10, 2026
Status: In Progress (Phase 2)
