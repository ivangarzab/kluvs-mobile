# 📚 Kluvs 🎬🍽️

[![Full Tests](https://github.com/ivangarzab/kluvs-mobile/actions/workflows/full-tests.yml/badge.svg)](https://github.com/ivangarzab/kluvs-mobile/actions/workflows/full-tests.yml)
[![codecov](https://codecov.io/gh/ivangarzab/kluvs-mobile/branch/main/graph/badge.svg)](https://codecov.io/gh/ivangarzab/kluvs-mobile)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg?logo=kotlin)](https://kotlinlang.org)

<p align="center">
  <img src="assets/ic_kluvs.png" alt="Kluvs Logo" width="200"/>
</p>

## ℹ️ About

**Kluvs** is a Kotlin Multiplatform mobile application for managing book clubs and reading sessions across Discord communities.

## ✨ Features

- 📖 **Book Club Management** - Create and join book clubs
- 👥 **Member Profiles** - Track participation
- 📅 **Session Tracking** - Keep up with reading schedules and discussions
- 🌐 **Cross-Platform** - Native apps for Android and iOS
- 🤖 **Companion Bot** - Discord companion bot available 
- 🔄 **Real-time Sync** - Powered by Supabase for live updates

## 🏗️ Tech Stack

- **Kotlin Multiplatform** - Shared business logic across platforms
- **Compose Multiplatform** - Modern declarative UI
- **Supabase** - Backend-as-a-Service for data and real-time features
- **Ktor** - Networking and API communication
- **Koin** - Dependency injection
- **[barK](https://github.com/ivangarzab/barK)** - Logging strategy for KMP
- **Kover** - Code coverage
- **Mokkery** - Testing framework

## 📂 Project Structure

```
kluvs-mobile/
├── composeApp/       # Compose Multiplatform UI code
│   ├── commonMain/   # Shared UI components
│   ├── androidMain/  # Android-specific code
│   └── iosMain/      # iOS-specific code
├── shared/           # Shared business logic
│   ├── commonMain/   # Core domain & data layers
│   ├── androidMain/  # Android-specific implementations
│   └── iosMain/      # iOS-specific implementations
└── iosApp/           # iOS application entry point
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (latest stable)
- **Xcode** 15+ (for iOS development)
- **JDK** 17+
- **Kotlin** 2.2.0+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/ivangarzab/kluvs-mobile.git
   cd kluvs-mobile
   ```

2. **Configure Supabase credentials**

   Create a `gradle.properties` file in your home directory (`~/.gradle/gradle.properties`) or in the project root:
   ```properties
   SUPABASE_URL=your_supabase_url
   SUPABASE_KEY=your_supabase_anon_key
   TEST_SUPABASE_URL=your_test_supabase_url
   TEST_SUPABASE_KEY=your_test_supabase_anon_key
   ```

3. **Run the Android app**
   ```bash
   ./gradlew :composeApp:assembleDebug
   ```

4. **Run the iOS app**
   - Open `iosApp/iosApp.xcodeproj` in Xcode
   - Select your target device/simulator
   - Press Run (⌘R)

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew shared:testDebugUnitTest
```

### Run Integration Tests (requires local Supabase)
```bash
./gradlew shared:testDebugUnitTest --tests "*IntegrationTest"
```

### Generate Coverage Report
```bash
./gradlew shared:koverHtmlReport
```
Reports are generated in `shared/build/reports/kover/html/`

## 🔄 CI/CD

The project uses GitHub Actions for continuous integration:

- **Unit Tests** - Fast feedback on every PR
- **Full Tests Suite** - Full test suite with Supabase on push to `main`
- **Code Coverage** - Tracked via Codecov

## 🙏 Acknowledgments

- Built with [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- Powered by [Supabase](https://supabase.com)
- Backend API: [kluvs-api](https://github.com/ivangarzab/kluvs-api)
- Discord companion bot: [quill-bot](https://github.com/ivangarzab/quill-bot)
- KMP Logging: [barK](https://github.com/ivangarzab/barK)

---

<p align="center"><i>Made with 🖤️ using Kotlin Multiplatform</i></p>
