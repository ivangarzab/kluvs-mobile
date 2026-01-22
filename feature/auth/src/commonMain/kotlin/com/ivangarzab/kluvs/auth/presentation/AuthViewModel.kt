package com.ivangarzab.kluvs.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.kluvs.auth.domain.AuthError
import com.ivangarzab.kluvs.auth.domain.AuthRepository
import com.ivangarzab.kluvs.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The purpose of this [ViewModel] class is to handle authentication logic for the
 * presentation layer.
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.initialize()
            authRepository.currentUser.collect { user: User? ->
                _state.update {
                    user?.let {
                        AuthState.Authenticated(it)
                    } ?: AuthState.Unauthenticated
                }
            }
        }
    }

    fun onEmailFieldChanged(value: String) {
        _uiState.update { it.copy(emailField = value, emailError = null) }
    }

    fun onPasswordFieldChanged(value: String) {
        _uiState.update { it.copy(passwordField = value, passwordError = null) }
    }

    fun onConfirmPasswordFieldChanged(value: String) {
        _uiState.update { it.copy(confirmPasswordField = value, confirmPasswordError = null) }
    }

    fun validateAndSignIn() {
        val emailError = when {
            _uiState.value.emailField.isBlank() -> "Email is required"
            !_uiState.value.emailField.contains("@") -> "Please enter a valid email"
            else -> null
        }

        val passwordError = when {
            _uiState.value.passwordField.isBlank() -> "Password is required"
            _uiState.value.passwordField.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return  // Don't proceed with API call
        }

        signIn()
    }

    fun validateAndSignUp() {
        val emailError = when {
            _uiState.value.emailField.isBlank() -> "Email is required"
            !_uiState.value.emailField.contains("@") -> "Please enter a valid email"
            else -> null
        }

        val passwordError = when {
            _uiState.value.passwordField.isBlank() -> "Password is required"
            _uiState.value.passwordField.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        val confirmPasswordError = when {
            _uiState.value.passwordField != _uiState.value.confirmPasswordField -> "Passowrds must match"
            else -> null
        }

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return  // Don't proceed with API call
        }

        signUp()
    }

    private fun signUp() = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.signUpWithEmail(
            email = uiState.value.emailField,
            password = uiState.value.passwordField
        ).onSuccess { user ->
            _state.update { AuthState.Authenticated(user) }
            clearForm()
        }.onFailure { error ->
            _state.update {
                AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
            }
        }
    }

    private fun signIn() = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.signInWithEmail(
            email = uiState.value.emailField,
            password = uiState.value.passwordField
        ).onSuccess { user ->
            _state.update { AuthState.Authenticated(user) }
            clearForm()
        }.onFailure { error ->
            _state.update {
                AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.signOut()
            .onSuccess { _state.update { AuthState.Unauthenticated } }
            .onFailure { error ->
                _state.update {
                    AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
                }
            }
    }

    /**
     * Initiates Discord OAuth sign-in flow.
     * Emits [AuthState.OAuthPending] with the OAuth URL to open in browser.
     */
    fun signInWithDiscord() = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.signInWithDiscord()
            .onSuccess { url ->
                _state.update { AuthState.OAuthPending(url) }
            }
            .onFailure { error ->
                _state.update {
                    AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
                }
            }
    }

    /**
     * Initiates Google OAuth sign-in flow.
     * Emits [AuthState.OAuthPending] with the OAuth URL to open in browser.
     */
    fun signInWithGoogle() = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.signInWithGoogle()
            .onSuccess { url ->
                _state.update { AuthState.OAuthPending(url) }
            }
            .onFailure { error ->
                _state.update {
                    AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
                }
            }
    }

    /**
     * Resets state to [AuthState.Unauthenticated].
     * Used when OAuth URL has been launched and we're waiting for callback.
     */
    fun onOAuthUrlLaunched() {
        _state.update { AuthState.Unauthenticated }
    }

    /**
     * Handles OAuth callback from deep link.
     * Should be called when the app receives an OAuth redirect.
     */
    fun handleOAuthCallback(callbackUrl: String) = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.handleOAuthCallback(callbackUrl)
            .onSuccess { user ->
                _state.update { AuthState.Authenticated(user) }
                clearForm()
            }
            .onFailure { error ->
                _state.update {
                    AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
                }
            }
    }

    /**
     * Signs in with Apple using a native ID token.
     * Called from iOS after native Apple Sign In returns the identity token.
     */
    fun signInWithApple(idToken: String) = viewModelScope.launch {
        _state.update { AuthState.Loading }
        authRepository.signInWithAppleNative(idToken)
            .onSuccess { user ->
                _state.update { AuthState.Authenticated(user) }
                clearForm()
            }
            .onFailure { error ->
                _state.update {
                    AuthState.Error(error as? AuthError ?: AuthError.UnexpectedError)
                }
            }
    }

    private fun clearForm() {
        _uiState.update { AuthUiState() }
    }
}

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val error: AuthError) : AuthState()
    /** OAuth flow initiated - UI should open the URL in a browser */
    data class OAuthPending(val url: String) : AuthState()
}