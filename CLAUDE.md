# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Kluvs** is a Kotlin Multiplatform mobile application for managing book clubs and reading sessions across Discord communities. The app uses Compose Multiplatform for UI and Supabase for backend services.

## Build Commands

### Android

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Build release APK
./gradlew :composeApp:assembleRelease

# Install and run on connected device
./gradlew :composeApp:installDebug
```

### iOS

- Open `iosApp/iosApp.xcodeproj` in Xcode
- Select target device/simulator
- Press Run (⌘R)

## Testing

### Run Unit Tests Only

```bash
# Run all unit tests (excludes integration tests)
./gradlew shared:testDebugUnitTest -PexcludeTests="**/*IntegrationTest.class"
```

### Run Integration Tests

Integration tests require a local Supabase instance. The full test suite runs integration tests:

```bash
# Run ALL tests including integration tests
./gradlew shared:testDebugUnitTest
```

**Note**: Integration tests connect to a Supabase instance specified by `TEST_SUPABASE_URL` and `TEST_SUPABASE_KEY` environment variables.

#### Setting Up Local Supabase for Integration Tests

Integration tests rely on a **local Supabase instance** running from the `kluvs-api` project. This ensures tests run against a consistent, isolated environment with seed data.

**Prerequisites:**
1. The `kluvs-api` project must be cloned at `/Users/ivangarzab/Git/kluvs-api`
2. Supabase CLI must be installed (`brew install supabase/tap/supabase`)

**Setup Workflow:**

```bash
# Navigate to the API project
cd /Users/ivangarzab/Git/kluvs-api

# Start local Supabase (if not already running)
npx supabase start

# Check status to get local credentials
npx supabase status
# Note: API URL is typically http://127.0.0.1:54321
# Note: anon key is provided in the status output
```

**Environment Configuration:**

Add these to your `~/.gradle/gradle.properties`:

```properties
TEST_SUPABASE_URL=http://127.0.0.1:54321
TEST_SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6ImFub24iLCJleHAiOjE5ODM4MTI5OTZ9.CRXP1A7WOeoJeXxjNni43kdQwgnWNReilDMblYTn_I0
```

**⚠️ Important: Applying Backend Changes**

When the backend API (`kluvs-api`) has new database migrations:

```bash
# Navigate to API project
cd /Users/ivangarzab/Git/kluvs-api

# Reset local database to apply new migrations and reseed
npx supabase db reset
```

This command:
- Drops and recreates the local database
- Applies ALL migrations in order (including new ones)
- Re-seeds test data from `supabase/seed.sql`
- Restarts Supabase containers

**Common Issues:**

1. **Tests failing with 404/NotFound errors**:
   - Likely cause: Backend migrations haven't been applied to local DB
   - Solution: Run `npx supabase db reset` in the API project

2. **Tests failing with schema errors**:
   - Likely cause: Stale database schema (missing migrations)
   - Solution: Run `npx supabase db reset` in the API project

3. **Connection errors**:
   - Verify local Supabase is running: `npx supabase status`
   - Verify `TEST_SUPABASE_URL` points to `http://127.0.0.1:54321`

**Test Data:**

Integration tests use seed data defined in `/Users/ivangarzab/Git/kluvs-api/supabase/seed.sql`. See test file headers (e.g., `ClubServiceIntegrationTest.kt:28-50`) for documentation of available test data.

### Run Specific Test

```bash
# Run a single test class
./gradlew shared:testDebugUnitTest --tests "com.ivangarzab.kluvs.data.repositories.ClubRepositoryTest"

# Run a single test method
./gradlew shared:testDebugUnitTest --tests "com.ivangarzab.kluvs.data.repositories.ClubRepositoryTest.testGetClubById"
```

### Code Coverage

```bash
# Generate HTML coverage report
./gradlew shared:koverHtmlReport

# Generate XML coverage report (for CI)
./gradlew shared:koverXmlReport
```

Reports are generated in `shared/build/reports/kover/html/`

## Architecture

### Module Structure

The project is organized into two main modules:

- **`shared/`** - Shared business logic and data layer (Kotlin Multiplatform)
  - Domain models
  - Data repositories
  - Remote data sources and API services
  - Dependency injection setup
- **`composeApp/`** - UI layer with Compose Multiplatform
  - Android-specific UI implementations currently
  - iOS support planned

### Data Layer Architecture

The data layer follows a clean architecture pattern with three layers:

1. **Services** (`data/remote/api/`) - Direct Supabase API communication via Supabase Functions
   - `ServerService`, `ClubService`, `MemberService`, `SessionService`
   - Handle raw API requests/responses

2. **Remote Data Sources** (`data/remote/source/`) - Transform DTOs to domain models
   - Use mappers to convert between DTOs and domain models
   - Handle data source-specific error handling

3. **Repositories** (`data/repositories/`) - Abstract data access for the domain layer
   - Expose clean domain interfaces
   - Currently delegate to remote data sources only
   - Designed to support local data sources in the future for caching/offline support

### Dependency Injection with Koin

All dependency injection is managed through Koin with modular organization:

- **`platformDataModule`** - Platform-specific dependencies (Android/iOS)
  - Android: `shared/src/androidMain/kotlin/com/ivangarzab/kluvs/di/DataModule.android.kt`
  - iOS: `shared/src/iosMain/kotlin/com/ivangarzab/kluvs/di/DataModule.ios.kt`
- **`remoteDataModule`** - API services and remote data sources
- **`repositoryModule`** - Repository implementations

Koin is initialized in `shared/src/commonMain/kotlin/com/ivangarzab/kluvs/di/KoinHelper.kt`

### Configuration Management

Supabase credentials are managed via BuildKonfig:

- Production credentials: `SUPABASE_URL`, `SUPABASE_KEY`
- Test credentials: `TEST_SUPABASE_URL`, `TEST_SUPABASE_KEY`

These must be set in `~/.gradle/gradle.properties` or as environment variables. See `shared/build.gradle.kts:87-122` for the configuration logic.

## Navigation Architecture

The app uses a hybrid navigation approach with two layers:

- **App-Level Navigation (Shared):** `AppCoordinator` handles authentication state and determines which major section of the app to show (login vs main app). This logic is shared across Android and iOS.
- **Feature Navigation (Platform-Specific):** Platform NavHost/NavigationStack handles user-driven flows like browsing clubs, viewing details, editing profile, etc.

See `docs/NAVIGATION.md` for detailed navigation architecture documentation.

## Testing Strategy

### Test Organization

- **Unit Tests** - Fast tests with mocked dependencies using Mokkery
  - Mappers, serializers, data sources, repositories
  - Located in `shared/src/commonTest/`

- **Integration Tests** - Tests against real Supabase instance
  - Suffixed with `*IntegrationTest.kt`
  - Require local Supabase instance running
  - Excluded from quick test runs via `excludeTests` property

### Logging in Tests

Tests use barK logging with a custom test rule (`BarkTestRule`) to capture and assert log output. Platform-specific implementations exist for Android and iOS.

## CI/CD

The project uses GitHub Actions:

- **Unit Tests** (`.github/workflows/unit-tests.yml`) - Fast feedback on PRs, excludes integration tests
- **Full Tests** (`.github/workflows/full-tests.yml`) - Runs on `main` branch with local Supabase instance
  - Checks out both `kluvs-mobile` and `kluvs-api` repos
  - Starts local Supabase instance
  - Applies migrations and seed data
  - Runs complete test suite
  - Uploads coverage to Codecov

## Gradle Configuration Notes

### Exclude Tests Dynamically

The `shared/build.gradle.kts` includes a custom task configuration (lines 137-149) that allows excluding tests via property:

```bash
./gradlew shared:testDebugUnitTest -PexcludeTests="**/*IntegrationTest.class"
```

### Kover Exclusions

Code coverage excludes:
- Generated code (`*.BuildConfig`, `*.BuildKonfig`)
- DTOs (`com.ivangarzab.kluvs.data.remote.dtos`)
- Dependency injection modules (`**.di`)