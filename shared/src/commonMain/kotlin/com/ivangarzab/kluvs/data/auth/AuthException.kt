package com.ivangarzab.kluvs.data.auth

/**
 * Represents authentication error codes.
 *
 * These error codes are locale-agnostic and should be mapped to
 * localized strings in the UI layer using platform-specific resources.
 */
sealed class AuthError : Exception() {
    /** Invalid email or password provided */
    data object InvalidCredentials : AuthError()

    /** Email address has not been verified */
    data object EmailNotConfirmed : AuthError()

    /** No internet connection available */
    data object NoConnection : AuthError()

    /** Too many authentication attempts */
    data object RateLimitExceeded : AuthError()

    /** User account not found */
    data object UserNotFound : AuthError()

    /** Password does not meet requirements */
    data object WeakPassword : AuthError()

    /** Email already registered */
    data object UserAlreadyExists : AuthError()

    /** Generic authentication failure */
    data object AuthenticationFailed : AuthError()

    /** Unexpected error occurred */
    data object UnexpectedError : AuthError()
}