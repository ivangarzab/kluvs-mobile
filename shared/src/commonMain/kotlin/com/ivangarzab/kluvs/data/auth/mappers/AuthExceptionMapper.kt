package com.ivangarzab.kluvs.data.auth.mappers

import com.ivangarzab.kluvs.data.auth.AuthError

/**
 * Maps any [Exception] into an [AuthError] code.
 *
 * These error codes are locale-agnostic. The UI layer is responsible
 * for mapping them to localized strings using platform resources.
 */
fun Exception.toAuthError(): AuthError {
    val errorMessage = this.message ?: return AuthError.UnexpectedError

    return when {
        // Invalid credentials
        errorMessage.contains("Invalid login credentials", ignoreCase = true) ->
            AuthError.InvalidCredentials

        // Email not confirmed
        errorMessage.contains("Email not confirmed", ignoreCase = true) ->
            AuthError.EmailNotConfirmed

        // Network errors
        errorMessage.contains("Unable to resolve host", ignoreCase = true) ||
                errorMessage.contains("Failed to connect", ignoreCase = true) ->
            AuthError.NoConnection

        // Rate limiting
        errorMessage.contains("Email rate limit exceeded", ignoreCase = true) ->
            AuthError.RateLimitExceeded

        // User not found
        errorMessage.contains("User not found", ignoreCase = true) ->
            AuthError.UserNotFound

        // Weak password (for sign up)
        errorMessage.contains("Password should be at least", ignoreCase = true) ->
            AuthError.WeakPassword

        // Email already exists (for sign up)
        errorMessage.contains("User already registered", ignoreCase = true) ->
            AuthError.UserAlreadyExists

        // Generic fallback
        else -> AuthError.AuthenticationFailed
    }
}