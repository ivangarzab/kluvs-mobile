package com.ivangarzab.kluvs.auth.mappers

import com.ivangarzab.kluvs.auth.domain.AuthError
import com.ivangarzab.kluvs.auth.domain.EmailConfirmationRequiredException
import com.ivangarzab.kluvs.auth.domain.UserAlreadyExistsException
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException

/**
 * Maps any [Exception] into an [AuthError] code.
 *
 * Prefers [AuthRestException.errorCode] — the real, versioned error code GoTrue returns in the
 * response body — over string-matching [Exception.message]. Message-matching is fragile: it
 * depends on exact English wording that can change across supabase-kt/GoTrue versions and isn't
 * guaranteed stable across platforms, whereas [AuthErrorCode] is a typed enum GoTrue commits to.
 * Message-matching remains as a fallback only for exceptions that aren't [AuthRestException], or
 * that carry an [AuthErrorCode] this app has no dedicated bucket for.
 *
 * These error codes are locale-agnostic. The UI layer is responsible
 * for mapping them to localized strings using platform resources.
 */
fun Exception.toAuthError(): AuthError = when (this) {
    is EmailConfirmationRequiredException -> AuthError.EmailConfirmationRequired
    is UserAlreadyExistsException -> AuthError.UserAlreadyExists
    is AuthRestException -> errorCode.toAuthError() ?: messageToAuthError()
    is HttpRequestTimeoutException -> AuthError.NoConnection
    is HttpRequestException -> AuthError.NoConnection
    else -> messageToAuthError()
}

private fun AuthErrorCode?.toAuthError(): AuthError? = when (this) {
    null -> null
    AuthErrorCode.InvalidCredentials -> AuthError.InvalidCredentials
    AuthErrorCode.EmailNotConfirmed -> AuthError.EmailNotConfirmed
    AuthErrorCode.OverRequestRateLimit,
    AuthErrorCode.OverEmailSendRateLimit,
    AuthErrorCode.OverSmsSendRateLimit -> AuthError.RateLimitExceeded
    AuthErrorCode.UserNotFound -> AuthError.UserNotFound
    AuthErrorCode.WeakPassword -> AuthError.WeakPassword
    AuthErrorCode.UserAlreadyExists,
    AuthErrorCode.EmailExists -> AuthError.UserAlreadyExists
    else -> AuthError.AuthenticationFailed
}

private fun Exception.messageToAuthError(): AuthError {
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