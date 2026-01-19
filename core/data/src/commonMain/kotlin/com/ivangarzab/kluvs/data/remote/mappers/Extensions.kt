package com.ivangarzab.kluvs.data.remote.mappers

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime

/**
 * String extension function to parse a date string that could be either:
 * - Date-only format: "2024-12-31"
 * - DateTime format: "2024-12-31T23:17:00"
 */
fun String.parseDateString(): LocalDateTime {
    return try {
        // Try parsing as full DateTime first
        LocalDateTime.parse(this)
    } catch (e: Exception) {
        // If that fails, parse as date-only and add midnight time
        LocalDate.parse(this).atTime(17, 0)
    }
}