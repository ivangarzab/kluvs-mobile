package com.ivangarzab.kluvs.presentation.util

import com.ivangarzab.kluvs.presentation.state.DateTimeFormat
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatDateTimeUseCaseTest {

    private val useCase = FormatDateTimeUseCase()

    @Test
    fun `FULL format returns complete date and time`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 19, 0, 0) // Feb 12, 2025 at 7:00 PM

        // When
        val result = useCase(dateTime, DateTimeFormat.FULL)

        // Then
        assertEquals("February 12, 2025 at 7:00 PM", result)
    }

    @Test
    fun `DATE_ONLY format returns date without time`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 19, 0, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.DATE_ONLY)

        // Then
        assertEquals("February 12, 2025", result)
    }

    @Test
    fun `TIME_ONLY format returns time without date`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 19, 0, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.TIME_ONLY)

        // Then
        assertEquals("7:00 PM", result)
    }

    @Test
    fun `YEAR_ONLY format returns only the year`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 19, 0, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.YEAR_ONLY)

        // Then
        assertEquals("2025", result)
    }

    @Test
    fun `formats midnight as 12 AM`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 0, 0, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.TIME_ONLY)

        // Then
        assertEquals("12:00 AM", result)
    }

    @Test
    fun `formats noon as 12 PM`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 12, 0, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.TIME_ONLY)

        // Then
        assertEquals("12:00 PM", result)
    }

    @Test
    fun `formats morning time correctly`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 9, 30, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.TIME_ONLY)

        // Then
        assertEquals("9:30 AM", result)
    }

    @Test
    fun `formats afternoon time correctly`() {
        // Given
        val dateTime = LocalDateTime(2025, 2, 12, 15, 45, 0)

        // When
        val result = useCase(dateTime, DateTimeFormat.TIME_ONLY)

        // Then
        assertEquals("3:45 PM", result)
    }

    @Test
    fun `formats all months correctly`() {
        // Test each month
        val months = listOf(
            1 to "January", 2 to "February", 3 to "March", 4 to "April",
            5 to "May", 6 to "June", 7 to "July", 8 to "August",
            9 to "September", 10 to "October", 11 to "November", 12 to "December"
        )

        months.forEach { (monthNum, monthName) ->
            // Given
            val dateTime = LocalDateTime(2025, monthNum, 1, 12, 0, 0)

            // When
            val result = useCase(dateTime, DateTimeFormat.DATE_ONLY)

            // Then
            assertEquals("$monthName 1, 2025", result)
        }
    }
}
