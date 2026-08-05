package com.ivangarzab.kluvs.presentation.error

import com.ivangarzab.kluvs.model.AppError

/**
 * The copy shown to the user for each [AppError] category. [AppError.ServerError] deliberately
 * never surfaces backend detail — 5xx bodies can contain raw Postgres error text (see
 * `Throwable.toAppError()` in `core:data`).
 */
fun AppError.toUserMessage(): String = when (this) {
    AppError.NoConnection -> "Check your connection and try again."
    AppError.Timeout -> "That took too long. Please try again."
    AppError.Unauthorized -> "Your session expired. Please sign in again."
    AppError.Forbidden -> "You don't have permission to do that."
    is AppError.NotFound -> detail ?: "We couldn't find that."
    is AppError.ValidationError -> detail ?: "Something about that request wasn't right."
    is AppError.Conflict -> detail ?: "That already exists."
    AppError.ServerError -> "Something went wrong on our end. Please try again."
    AppError.Unknown -> "Something went wrong. Please try again."
}
