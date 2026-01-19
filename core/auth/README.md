# :core:auth

Authentication business logic and data layer.

## Purpose

This module handles all authentication concerns: signing in/out, session management, token storage, and auth state. It provides the `AuthRepository` interface that other modules depend on.

## Key Components

### AuthRepository

Exposes:
- `currentUser: StateFlow<User?>` - Current authenticated user
- `isAuthenticated: StateFlow<Boolean>` - Auth state
- `signInWithEmail()`, `signUpWithEmail()`, `signOut()`
- `signInWithDiscord()`, `signInWithGoogle()` - OAuth flows

### SecureStorage

Platform-specific secure token storage:
- **Android**: EncryptedSharedPreferences
- **iOS**: Keychain

## Dependencies

- `:core:model` - Domain models
- `:core:network` - Supabase client

## Usage

```kotlin
class MyViewModel(private val authRepo: AuthRepository) {
    val isLoggedIn = authRepo.isAuthenticated

    fun logout() {
        viewModelScope.launch {
            authRepo.signOut()
        }
    }
}
```
