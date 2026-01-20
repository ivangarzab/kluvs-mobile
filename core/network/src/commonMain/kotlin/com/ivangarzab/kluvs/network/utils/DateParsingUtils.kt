package com.ivangarzab.kluvs.network.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Parses a date-only string (YYYY-MM-DD format) into a [LocalDate].
 * Returns null if the string is null or cannot be parsed.
 */
fun parseDateOnlyString(dateString: String?): LocalDate? {
    if (dateString == null) return null
    return try {
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        null
    }
}

/**
 * Parses a timestamp string (ISO 8601 format) into a [LocalDateTime].
 * Handles various timestamp formats:
 * - Full ISO 8601 with timezone: "2024-01-15T10:30:00+00:00"
 * - ISO 8601 with Z suffix: "2024-01-15T10:30:00Z"
 * - Date-only format (time defaults to 00:00:00): "2024-01-15"
 *
 * Returns null if the string is null or cannot be parsed.
 */
fun parseDateTimeString(dateString: String?): LocalDateTime? {
    if (dateString == null) return null
    return try {
        // Parse ISO 8601 timestamp, strip timezone suffix
        LocalDateTime.parse(dateString.substringBefore("+").substringBefore("Z"))
    } catch (e: Exception) {
        try {
            // Fallback: date-only format
            val datePart = dateString.split("T")[0]
            LocalDateTime.parse("${datePart}T00:00:00")
        } catch (e: Exception) {
            null
        }
    }
}
