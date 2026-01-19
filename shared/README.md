# :shared

Application integration module and iOS framework.

## Purpose

This module serves two critical purposes:

1. **iOS Framework**: Exports all necessary modules as a single iOS framework (`Shared.framework`) for the iOS app to consume
2. **App Coordination**: Contains cross-platform components that coordinate app-level behavior

## Key Components

### KoinHelper

Initializes Koin dependency injection with all modules:
- Platform modules (Android/iOS specific)
- Core modules (network, data, auth, presentation)
- Feature modules (auth, clubs, member)

### AppCoordinator

Observes authentication state and determines which part of the app to show:
- `Initializing` - App is starting, checking for existing session
- `Login` - User needs to authenticate
- `Main` - User is authenticated, show main app

## iOS Framework Exports

The following modules are exported for iOS:
- `:core:model`
- `:core:presentation`
- `:core:auth`
- `:feature:auth`
- `:feature:clubs`
- `:feature:member`

## Dependencies

This module depends on all core and feature modules to aggregate them:
- All `:core:*` modules
- All `:feature:*` modules

## Usage

### Android (in Application class)

```kotlin
class KluvsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@KluvsApplication)
        }
    }
}
```

### iOS (in AppDelegate)

```swift
KoinHelperKt.doInitKoin()
```
