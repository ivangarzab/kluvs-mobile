package com.ivangarzab.kluvs.domain.usecases.util

import com.ivangarzab.kluvs.presentation.models.DateTimeFormat
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

/**
 * UseCase for formatting date/time values for UI display.
 *
 * Provides consistent date/time formatting across the application.
 * Uses kotlinx-datetime for multiplatform support.
 *
 * @see DateTimeFormat for available formatting options
 */
class FormatDateTimeUseCase {

    /**
     * Formats a [LocalDateTime] according to the specified format.
     *
     * @param dateTime The date and time to format
     * @param format The desired output format
     * @return Formatted string ready for UI display
     *
     * Examples:
     * - FULL: "February 12, 2025 at 7:00 PM"
     * - DATE_ONLY: "February 12, 2025"
     * - TIME_ONLY: "7:00 PM"
     * - YEAR_ONLY: "2026"
     */
    operator fun invoke(dateTime: LocalDateTime, format: DateTimeFormat): String {
        return when (format) {
            DateTimeFormat.FULL -> {
                val date = dateTime.date
                val time = dateTime.time
                "${formatMonthName(date.monthNumber)} ${date.dayOfMonth}, ${date.year} at ${formatTime(time)}"
            }
            DateTimeFormat.DATE_ONLY -> {
                val date = dateTime.date
                "${formatMonthName(date.monthNumber)} ${date.dayOfMonth}, ${date.year}"
            }
            DateTimeFormat.TIME_ONLY -> {
                formatTime(dateTime.time)
            }
            DateTimeFormat.YEAR_ONLY -> {
                dateTime.date.year.toString()
            }
        }
    }

    /**
     * Formats a time in 12-hour format with AM/PM.
     *
     * @param time The time to format
     * @return Formatted time string (e.g., "7:00 PM")
     */
    private fun formatTime(time: LocalTime): String {
        val hour = if (time.hour == 0 || time.hour == 12) 12 else time.hour % 12
        val minute = time.minute.toString().padStart(2, '0')
        val amPm = if (time.hour < 12) "AM" else "PM"
        return "$hour:$minute $amPm"
    }

    /**
     * Converts month number (1-12) to full month name.
     *
     * @param monthNumber The month number (1 = January, 12 = December)
     * @return Full month name
     */
    private fun formatMonthName(monthNumber: Int): String {
        return when (monthNumber) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }
    }
}
