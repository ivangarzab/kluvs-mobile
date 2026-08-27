package com.ivangarzab.kluvs.data.error

import com.ivangarzab.kluvs.api.models.ErrorDto
import com.ivangarzab.kluvs.model.AppError
import com.ivangarzab.kluvs.network.utils.JsonHelper.getJsonForSupabaseService
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException

/**
 * Maps a raw exception from the Supabase client into the small [AppError] vocabulary the UI
 * layer understands. [RestException.statusCode] (not the exception's own subclass) is the
 * classification key for anything beyond [BadRequestRestException]/[NotFoundRestException] —
 * `Functions.parseErrorResponse` in supabase-kt 3.2.6 falls back to `UnauthorizedRestException`
 * for every status code it doesn't special-case (403, 409, 500, ...), so the subclass name alone
 * is not trustworthy; `statusCode` always reflects the real HTTP response.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is BadRequestRestException -> AppError.ValidationError(backendDetail())
    is NotFoundRestException -> AppError.NotFound(backendDetail())
    is RestException -> byStatusCode()
    is HttpRequestTimeoutException -> AppError.Timeout
    is HttpRequestException -> AppError.NoConnection
    else -> AppError.Unknown
}

/**
 * The backend's `error` string for 4xx categories is already a clean, human-written sentence
 * (see design-system error copy audit) — safe to surface directly, unlike 5xx bodies which can
 * leak raw Postgres error text. [RestException.error] is the *raw response body*, not the parsed
 * field, so it's decoded here rather than shown as-is.
 */
private fun RestException.backendDetail(): String? = runCatching {
    getJsonForSupabaseService().decodeFromString(ErrorDto.serializer(), error).error
}.getOrNull()?.takeIf { it.isNotBlank() }

private fun RestException.byStatusCode(): AppError = when (statusCode) {
    401 -> AppError.Unauthorized
    403 -> AppError.Forbidden
    404 -> AppError.NotFound(backendDetail())
    409 -> AppError.Conflict(backendDetail())
    in 500..599 -> AppError.ServerError
    else -> AppError.Unknown
}
