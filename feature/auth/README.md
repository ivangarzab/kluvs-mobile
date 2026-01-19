# :feature:auth

Authentication UI feature module.

## Purpose

This module contains the presentation layer for authentication flows: sign in, sign up, and OAuth handling. It provides the `AuthViewModel` that manages auth UI state and user interactions.

## Key Components

### AuthViewModel

Manages:
- Form field state (email, password, confirm password)
- Field validation with error messages
- Sign in / sign up flows
- OAuth flows (Discord, Google)
- Auth state (`Unauthenticated`, `Loading`, `Authenticated`, `Error`)

### AuthUiState

```kotlin
data class AuthUiState(
    val emailField: String,
    val passwordField: String,
    val confirmPasswordField: String,
    val emailError: String?,
    val passwordError: String?,
    val confirmPasswordError: String?
)
```

## Dependencies

- `:core:model` - User model
- `:core:auth` - AuthRepository
- `:core:presentation` - Base presentation utilities

## Usage

```kotlin
@Composable
fun AuthScreen(viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    when (state) {
        is AuthState.Authenticated -> navigateToMain()
        is AuthState.Error -> showError((state as AuthState.Error).error)
        else -> ShowLoginForm(uiState, viewModel)
    }
}
```
