package com.ivangarzab.kluvs.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.auth.domain.AuthError

/**
 * Maps [com.ivangarzab.kluvs.auth.AuthError] codes to localized string resources.
 *
 * Usage in Composables:
 * ```
 * val errorMessage = error.toLocalizedMessage()
 * Text(text = errorMessage)
 * ```
 */
@Composable
fun AuthError.toLocalizedMessage(): String = when (this) {
    is AuthError.InvalidCredentials -> stringResource(R.string.error_invalid_credentials)
    is AuthError.EmailNotConfirmed -> stringResource(R.string.error_email_not_confirmed)
    is AuthError.NoConnection -> stringResource(R.string.error_no_connection)
    is AuthError.RateLimitExceeded -> stringResource(R.string.error_rate_limit_exceeded)
    is AuthError.UserNotFound -> stringResource(R.string.error_user_not_found)
    is AuthError.WeakPassword -> stringResource(R.string.error_weak_password)
    is AuthError.UserAlreadyExists -> stringResource(R.string.error_user_already_exists)
    is AuthError.AuthenticationFailed -> stringResource(R.string.error_authentication_failed)
    is AuthError.UnexpectedError -> stringResource(R.string.error_unexpected)
}
