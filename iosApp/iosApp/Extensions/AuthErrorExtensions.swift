//
//  AuthErrorExtensions.swift
//  iosApp
//
//  Extension to convert Kotlin AuthError to localized error messages
//
import Shared

extension Shared.AuthError {
    func toLocalizedMessage() -> String {
        switch self {
        case is Shared.AuthError.InvalidCredentials:
            return NSLocalizedString("error_invalid_credentials", comment: "")
        case is Shared.AuthError.EmailNotConfirmed:
            return NSLocalizedString("error_email_not_confirmed", comment: "")
        case is Shared.AuthError.NoConnection:
            return NSLocalizedString("error_no_connection", comment: "")
        case is Shared.AuthError.RateLimitExceeded:
            return NSLocalizedString("error_rate_limit_exceeded", comment: "")
        case is Shared.AuthError.UserNotFound:
            return NSLocalizedString("error_user_not_found", comment: "")
        case is Shared.AuthError.WeakPassword:
            return NSLocalizedString("error_weak_password", comment: "")
        case is Shared.AuthError.UserAlreadyExists:
            return NSLocalizedString("error_user_already_exists", comment: "")
        case is Shared.AuthError.AuthenticationFailed:
            return NSLocalizedString("error_authentication_failed", comment: "")
        default:
            return NSLocalizedString("error_unexpected", comment: "")
        }
    }
}
