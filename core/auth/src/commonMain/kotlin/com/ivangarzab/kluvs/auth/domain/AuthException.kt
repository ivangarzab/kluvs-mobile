package com.ivangarzab.kluvs.auth.domain

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

    /** Sign up succeeded but requires email confirmation before a session can be created */
    data object EmailConfirmationRequired : AuthError()

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

/**
 * Thrown when email sign up succeeds but Supabase requires email confirmation before a
 * session is created (i.e. no session exists yet, but no auth failure occurred either).
 */
class EmailConfirmationRequiredException :
    Exception("Sign up succeeded but requires email confirmation")

/**
 * Thrown when signing up with an email that's already registered.
 *
 * GoTrue doesn't return an error for this case when email confirmations are enabled — to avoid
 * leaking which emails are registered, it responds with a fake "success" (null session) that's
 * indistinguishable from a genuine new sign-up pending confirmation, except that [io.github.jan.supabase.auth.user.UserInfo.identities]
 * comes back empty instead of containing the newly created identity.
 */
class UserAlreadyExistsException : Exception("An account with this email already exists")