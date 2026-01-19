# Module Dependency Graph

This document visualizes the Gradle module dependency structure for Kluvs.

## Overview

```mermaid
graph TB
    subgraph "App Layer"
        composeApp[":composeApp<br/>(Android UI)"]
        iosApp["iosApp<br/>(iOS UI)"]
    end

    subgraph "Integration Layer"
        shared[":shared<br/>(iOS Framework + DI)"]
    end

    subgraph "Feature Modules"
        feature_auth[":feature:auth<br/>(Auth UI)"]
        feature_clubs[":feature:clubs<br/>(Clubs UI)"]
        feature_member[":feature:member<br/>(Profile UI)"]
    end

    subgraph "Core Modules"
        core_presentation[":core:presentation<br/>(UI Utilities)"]
        core_auth[":core:auth<br/>(Auth Logic)"]
        core_data[":core:data<br/>(Repositories)"]
        core_network[":core:network<br/>(Supabase Client)"]
        core_model[":core:model<br/>(Domain Models)"]
    end

    %% App dependencies
    composeApp --> shared
    iosApp --> shared

    %% Shared aggregates everything
    shared --> feature_auth
    shared --> feature_clubs
    shared --> feature_member
    shared --> core_auth
    shared --> core_data
    shared --> core_presentation
    shared --> core_network

    %% Feature dependencies
    feature_auth --> core_auth
    feature_auth --> core_presentation
    feature_auth --> core_model

    feature_clubs --> core_data
    feature_clubs --> core_auth
    feature_clubs --> core_presentation
    feature_clubs --> core_model

    feature_member --> core_data
    feature_member --> core_auth
    feature_member --> core_presentation
    feature_member --> core_model

    %% Core dependencies
    core_presentation --> core_model
    core_auth --> core_network
    core_auth --> core_model
    core_data --> core_network
    core_data --> core_auth
    core_data --> core_model
    core_network --> core_model
```

## Dependency Flow

### Vertical Layers

```
┌─────────────────────────────────────────────┐
│              App Layer                       │
│         (composeApp, iosApp)                │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│           Integration Layer                  │
│              (shared)                        │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│           Feature Layer                      │
│   (feature:auth, feature:clubs,             │
│            feature:member)                   │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│             Core Layer                       │
│  (core:presentation, core:auth, core:data)  │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│          Foundation Layer                    │
│      (core:network, core:model)             │
└─────────────────────────────────────────────┘
```

## Module Descriptions

| Module | Type | Purpose |
|--------|------|---------|
| `:composeApp` | App | Android application with Compose UI |
| `iosApp` | App | iOS application (Xcode project) |
| `:shared` | Integration | iOS framework export + DI setup |
| `:feature:auth` | Feature | Authentication screens |
| `:feature:clubs` | Feature | Club details and sessions |
| `:feature:member` | Feature | User profile and stats |
| `:core:presentation` | Core | Shared UI utilities |
| `:core:auth` | Core | Authentication business logic |
| `:core:data` | Core | Repository pattern implementation |
| `:core:network` | Core | Supabase client configuration |
| `:core:model` | Core | Domain model definitions |

## Dependency Rules

1. **App modules** only depend on `:shared`
2. **Feature modules** depend on core modules, never on each other
3. **Core modules** can depend on other core modules at the same or lower level
4. **`:core:model`** has no internal dependencies (leaf module)
5. **`:shared`** aggregates all modules for iOS framework export

## API vs Implementation

- `api()` - Exposes types transitively (used for public APIs)
- `implementation()` - Internal dependency (encapsulated)

### Feature Module Pattern

```kotlin
// feature:clubs/build.gradle.kts
commonMain.dependencies {
    api(project(":core:model"))           // Exposed: UI models use domain types
    implementation(project(":core:data")) // Internal: repository usage
    implementation(project(":core:auth")) // Internal: auth checks
    implementation(project(":core:presentation")) // Internal: formatting
}
```
