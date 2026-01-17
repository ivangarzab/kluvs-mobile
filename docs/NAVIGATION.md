# Navigation Architecture

This document describes the navigation strategy used in the Kluvs mobile app.

## Overview

The app uses a **hybrid navigation approach**:
- **App-level navigation** (cross-platform, shared code) - Determines which major section of the app to show
- **Feature navigation** (platform-specific) - Handles user-driven flows within each section

This balances code reuse with platform-appropriate navigation patterns.

---

## App-Level Navigation (Shared)

### AppCoordinator

**Location:** `shared/src/commonMain/kotlin/com/ivangarzab/kluvs/app/AppCoordinator.kt`

**Purpose:** Observes app-wide state (authentication, onboarding, etc.) and emits navigation state for platforms to act on.

**Responsibilities:**
- Monitor authentication state via `AuthRepository`
- Initialize auth on app start (restore session if exists)
- Emit `NavigationState` changes
- Future: Handle onboarding, force updates, maintenance mode

**Does NOT:**
- Perform actual navigation (platform handles that)
- Manage feature-level navigation
- Know about platform navigation APIs

### NavigationState

```kotlin
sealed class NavigationState {
    data object Initializing : NavigationState()
    data object Unauthenticated : NavigationState()
    data class Authenticated(val userId: String) : NavigationState()
}
```

**States:**
- `Initializing` - App is starting up, checking auth status
- `Unauthenticated` - User needs to log in
- `Authenticated(userId)` - User is logged in with given userId

---

## Platform Navigation (Android)

### MainActivity Integration

**Location:** `composeApp/src/androidMain/kotlin/com/ivangarzab/kluvs/ui/MainActivity.kt`

**How it works:**

1. **Observes AppCoordinator:**
   ```kotlin
   val appCoordinator: AppCoordinator = koinViewModel()
   val navState by appCoordinator.navigationState.collectAsState()
   ```

2. **Auto-navigates based on state:**
   ```kotlin
   LaunchedEffect(navState) {
       when (navState) {
           is NavigationState.Unauthenticated -> navigate(LOGIN)
           is NavigationState.Authenticated -> navigate(MAIN)
           NavigationState.Initializing -> { /* Wait */ }
       }
   }
   ```

3. **Clears backstack on major transitions:**
   ```kotlin
   navController.navigate(destination) {
       popUpTo(0) { inclusive = true }  // Clear all previous screens
   }
   ```

### Feature Navigation (Platform-Specific)

Feature navigation stays in `NavHost` composables and uses platform-native APIs:

```kotlin
NavHost(...) {
    // Auth flow
    composable(LOGIN) { LoginScreen(...) }
    composable(SIGNUP) { SignupScreen(...) }
    composable(FORGOT_PASSWORD) { ForgotPasswordScreen(...) }

    // Main app
    composable(MAIN) { MainScreen(...) }

    // Future: Feature screens
    composable("club_details/{id}") { ClubDetailsScreen(...) }
    composable("profile_edit") { ProfileEditScreen(...) }
}
```

**Navigation callbacks are explicit:**
```kotlin
LoginScreen(
    onNavigateToSignUp = { navController.navigate(SIGNUP) },
    onNavigateToForgotPassword = { navController.navigate(FORGOT_PASSWORD) },
    onNavigateToMain = { /* Handled by AppCoordinator */ }
)
```

---

## Auth Flow Integration

### The Complete Flow

1. **User clicks "Sign Out"**
   ```
   MeScreen → MeViewModel.signOut()
   ```

2. **Auth repository updates**
   ```
   AuthRepository.signOut() → currentUser = null
   ```

3. **AppCoordinator reacts**
   ```
   AppCoordinator observes currentUser → emits NavigationState.Unauthenticated
   ```

4. **MainActivity navigates**
   ```
   MainActivity observes NavigationState → navigate(LOGIN)
   ```

**No race conditions.** Navigation is driven by state changes, not manual calls.

### Why Two ViewModels Observe AuthRepository?

Both `AppCoordinator` and `AuthViewModel` observe `AuthRepository.currentUser`, but for **different purposes**:

- **AppCoordinator**: "Where should the user be in the app?" (navigation)
- **AuthViewModel**: "What's the auth operation status?" (loading, errors, form state)

This is intentional separation of concerns, not duplication.

---

## iOS Implementation (Future)

iOS will follow the same pattern using SwiftUI:

```swift
@StateObject var appCoordinator = AppCoordinator()

var body: some View {
    NavigationStack {
        switch appCoordinator.navState {
        case .unauthenticated:
            LoginView()
        case .authenticated(let userId):
            MainView(userId: userId)
        case .initializing:
            LoadingView()
        }
    }
}
```

**Same logic, different execution.**

---

## Design Decisions

### Why Not Full Shared Navigation?

**Option:** Create a shared navigation library that works on both platforms

**Rejected because:**
- Android and iOS have fundamentally different navigation patterns
- Would require abstracting over Jetpack Navigation + SwiftUI NavigationStack
- Loses platform-native features (swipe back, deep links, SavedState, etc.)
- Higher complexity for minimal benefit
- Feature navigation is 1-2 lines per screen - duplication cost is low

### Why Not Just Platform Navigation?

**Option:** Skip AppCoordinator, handle everything in MainActivity

**Rejected because:**
- Navigation logic would be duplicated across Android and iOS
- Harder to test (platform-dependent)
- Loses single source of truth for app-level state
- Auth state management would be platform-specific

### The Hybrid Approach (Current)

**App-level navigation** (auth, onboarding) is complex, affects both platforms, and should be shared.

**Feature navigation** (club details, profile) is simple, platform-appropriate, and duplicating it is fine.

**Best of both worlds.**

---

## Future Extensions

### Adding New App-Level States

To add a new app-level navigation state (e.g., onboarding):

1. **Update NavigationState:**
   ```kotlin
   sealed class NavigationState {
       // ... existing
       data object OnboardingRequired : NavigationState()
   }
   ```

2. **Update AppCoordinator:**
   ```kotlin
   init {
       viewModelScope.launch {
           // Check if user completed onboarding
           if (!onboardingRepository.isCompleted()) {
               _navigationState.value = NavigationState.OnboardingRequired
           }
       }
   }
   ```

3. **Update MainActivity:**
   ```kotlin
   when (navState) {
       // ... existing
       NavigationState.OnboardingRequired -> navigate(ONBOARDING)
   }
   ```

4. **iOS does the same in SwiftUI**

### Adding New Feature Screens

Just add to platform `NavHost`:

```kotlin
composable("club_details/{id}") { backStackEntry ->
    val clubId = backStackEntry.arguments?.getString("id")
    ClubDetailsScreen(
        clubId = clubId,
        onNavigateBack = { navController.popBackStack() }
    )
}
```

No changes to AppCoordinator needed.

---

## Testing Strategy

### AppCoordinator Tests

```kotlin
@Test
fun `when user signs out, emits Unauthenticated state`() {
    // Mock AuthRepository
    // Trigger signOut()
    // Assert navigationState = Unauthenticated
}
```

### Navigation Integration Tests

```kotlin
@Test
fun `when authenticated, MainActivity navigates to MAIN`() {
    // Set AppCoordinator state to Authenticated
    // Assert current screen is MainScreen
}
```

---

## Common Pitfalls

### ❌ Don't: Call navigation manually in ViewModels

```kotlin
// ❌ Bad
fun signOut() {
    authRepository.signOut()
    onNavigateToLogin()  // ViewModel shouldn't know about navigation
}
```

```kotlin
// ✅ Good
fun signOut() {
    authRepository.signOut()
    // AppCoordinator handles navigation
}
```

### ❌ Don't: Put feature navigation in AppCoordinator

```kotlin
// ❌ Bad - AppCoordinator shouldn't know about club details
sealed class NavigationState {
    data class ShowClubDetails(val clubId: String) : NavigationState()
}
```

```kotlin
// ✅ Good - Handle in platform NavHost
composable("club_details/{id}") { ... }
```

### ✅ Do: Keep app-level and feature navigation separate

**App-level:** Authentication, onboarding, force updates
**Feature-level:** Everything else

---

## Summary

**Navigation = Two Layers**

1. **App-Level (Shared):** AppCoordinator determines major sections
2. **Feature-Level (Platform):** NavHost/NavigationStack handles flows

**Benefits:**
- ✅ Auth logic shared across platforms
- ✅ Platform-native navigation patterns
- ✅ Testable, maintainable, scalable
- ✅ Single source of truth for app state

**Tradeoffs:**
- ⚠️ Some navigation logic is platform-specific (but minimal)
- ⚠️ Two ViewModels observe AuthRepository (but for different purposes)

**Good enough to ship. Perfect is the enemy of good.**