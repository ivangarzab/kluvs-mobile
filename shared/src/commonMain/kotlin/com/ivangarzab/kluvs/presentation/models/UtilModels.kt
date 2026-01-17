package com.ivangarzab.kluvs.presentation.models

/**
 * Date/time formatting options for UI display.
 *
 * Used by FormatDateTimeUseCase to produce consistent formatting.
 */
enum class DateTimeFormat {
    /**
     * Full date and time format.
     * Example: "February 12, 2025 at 7:00 PM"
     */
    FULL,

    /**
     * Date only format.
     * Example: "February 12, 2025"
     */
    DATE_ONLY,

    /**
     * Time only format.
     * Example: "7:00 PM"
     */
    TIME_ONLY,

    /**
     * Year only format.
     * Example: "2026"
     */
    YEAR_ONLY,
}
