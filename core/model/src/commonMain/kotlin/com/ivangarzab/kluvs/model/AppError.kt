package com.ivangarzab.kluvs.model

/**
 * The small, closed set of failure categories the UI actually needs to distinguish. Raw
 * exceptions (HTTP client diagnostics, Postgres error text leaking through 5xx responses, etc.)
 * are mapped down to one of these at the data layer via `Throwable.toAppError()` in `core:data`,
 * so presentation code never has to branch on transport-level exception types or show users a
 * developer-facing message. [ValidationError], [NotFound], and [Conflict] carry [detail] because
 * the backend's `error` string for those categories is already a clean, human-readable sentence
 * (e.g. "Club name is required") — safe to surface directly, unlike 5xx bodies.
 */
sealed class AppError(message: String) : Exception(message) {
    data object NoConnection : AppError("No connection")
    data object Timeout : AppError("Request timed out")
    data object Unauthorized : AppError("Unauthorized")
    data object Forbidden : AppError("Forbidden")
    data class NotFound(val detail: String? = null) : AppError(detail ?: "Not found")
    data class ValidationError(val detail: String? = null) : AppError(detail ?: "Invalid request")
    data class Conflict(val detail: String? = null) : AppError(detail ?: "Conflict")
    data object ServerError : AppError("Server error")
    data object Unknown : AppError("Unknown error")
}
