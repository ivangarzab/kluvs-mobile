package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.DiscussionDto
import com.ivangarzab.kluvs.model.Discussion
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiscussionMappersTest {

    // ========================================
    // toDomain() TESTS
    // ========================================

    @Test
    fun `toDomain maps all fields correctly`() {
        // Given: A DiscussionDto with all fields
        val dto = DiscussionDto(
            id = "disc-1",
            session_id = "session-123",
            title = "Chapter 1 Discussion",
            date = "2024-06-15T18:00:00",
            location = "Discord Voice Channel"
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: All fields are mapped correctly
        assertEquals("disc-1", domain.id)
        assertEquals("session-123", domain.sessionId)
        assertEquals("Chapter 1 Discussion", domain.title)
        assertEquals(LocalDateTime(2024, 6, 15, 18, 0, 0), domain.date)
        assertEquals("Discord Voice Channel", domain.location)
    }

    @Test
    fun `toDomain handles nullable fields correctly`() {
        // Given: A DiscussionDto with nullable fields null
        val dto = DiscussionDto(
            id = "disc-2",
            session_id = null,
            title = "Final Discussion",
            date = "2024-12-31T20:00:00",
            location = null
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: Nullable fields are null
        assertEquals("disc-2", domain.id)
        assertNull(domain.sessionId)
        assertEquals("Final Discussion", domain.title)
        assertEquals(LocalDateTime(2024, 12, 31, 20, 0, 0), domain.date)
        assertNull(domain.location)
    }

    @Test
    fun `toDomain parses ISO 8601 date correctly`() {
        // Given: A DiscussionDto with various date formats
        val dto1 = DiscussionDto(
            id = "disc-3",
            session_id = null,
            title = "Test",
            date = "2024-01-01T00:00:00",
            location = null
        )

        // When: Mapping to domain
        val domain1 = dto1.toDomain()

        // Then: Date is parsed correctly
        assertEquals(LocalDateTime(2024, 1, 1, 0, 0, 0), domain1.date)
    }

    // ========================================
    // toDto() TESTS
    // ========================================

    @Test
    fun `toDto maps all fields correctly`() {
        // Given: A Discussion domain model with all fields
        val discussion = Discussion(
            id = "disc-1",
            sessionId = "session-123",
            title = "Chapter 1 Discussion",
            date = LocalDateTime(2024, 6, 15, 18, 0, 0),
            location = "Discord Voice Channel"
        )

        // When: Mapping to DTO
        val dto = discussion.toDto()

        // Then: All fields are mapped correctly
        assertEquals("disc-1", dto.id)
        assertEquals("session-123", dto.session_id)
        assertEquals("Chapter 1 Discussion", dto.title)
        assertEquals("2024-06-15T18:00", dto.date)
        assertEquals("Discord Voice Channel", dto.location)
    }

    @Test
    fun `toDto handles nullable fields correctly`() {
        // Given: A Discussion domain model with nullable fields null
        val discussion = Discussion(
            id = "disc-2",
            sessionId = null,
            title = "Final Discussion",
            date = LocalDateTime(2024, 12, 31, 20, 0, 0),
            location = null
        )

        // When: Mapping to DTO
        val dto = discussion.toDto()

        // Then: Nullable fields are null
        assertEquals("disc-2", dto.id)
        assertNull(dto.session_id)
        assertEquals("Final Discussion", dto.title)
        assertEquals("2024-12-31T20:00", dto.date)
        assertNull(dto.location)
    }

    @Test
    fun `toDto converts LocalDateTime to ISO 8601 string`() {
        // Given: A Discussion with a specific date
        val discussion = Discussion(
            id = "disc-3",
            sessionId = "session-456",
            title = "Test Discussion",
            date = LocalDateTime(2024, 1, 1, 0, 0, 0),
            location = "Library"
        )

        // When: Mapping to DTO
        val dto = discussion.toDto()

        // Then: Date is converted to ISO 8601 string format
        assertEquals("2024-01-01T00:00", dto.date)
    }

    @Test
    fun `toDto handles different date times correctly`() {
        // Given: A Discussion with various date/time combinations
        val discussion1 = Discussion(
            id = "disc-4",
            sessionId = "session-789",
            title = "Morning Discussion",
            date = LocalDateTime(2024, 3, 15, 9, 30, 45),
            location = "Coffee Shop"
        )

        val discussion2 = Discussion(
            id = "disc-5",
            sessionId = "session-789",
            title = "Evening Discussion",
            date = LocalDateTime(2024, 11, 25, 23, 59, 59),
            location = "Online"
        )

        // When: Mapping to DTO
        val dto1 = discussion1.toDto()
        val dto2 = discussion2.toDto()

        // Then: Dates are converted correctly
        assertEquals("2024-03-15T09:30:45", dto1.date)
        assertEquals("2024-11-25T23:59:59", dto2.date)
    }

    @Test
    fun `toDto roundtrip preserves all data`() {
        // Given: A Discussion domain model
        val originalDiscussion = Discussion(
            id = "disc-roundtrip",
            sessionId = "session-999",
            title = "Roundtrip Test",
            date = LocalDateTime(2024, 7, 4, 14, 30, 0),
            location = "Community Center"
        )

        // When: Converting to DTO and back to domain
        val dto = originalDiscussion.toDto()
        val reconvertedDiscussion = dto.toDomain()

        // Then: All fields are preserved
        assertEquals(originalDiscussion.id, reconvertedDiscussion.id)
        assertEquals(originalDiscussion.sessionId, reconvertedDiscussion.sessionId)
        assertEquals(originalDiscussion.title, reconvertedDiscussion.title)
        assertEquals(originalDiscussion.date, reconvertedDiscussion.date)
        assertEquals(originalDiscussion.location, reconvertedDiscussion.location)
    }

    @Test
    fun `toDto roundtrip with null fields preserves null values`() {
        // Given: A Discussion domain model with null optional fields
        val originalDiscussion = Discussion(
            id = "disc-null-roundtrip",
            sessionId = null,
            title = "Null Fields Test",
            date = LocalDateTime(2024, 8, 20, 16, 0, 0),
            location = null
        )

        // When: Converting to DTO and back to domain
        val dto = originalDiscussion.toDto()
        val reconvertedDiscussion = dto.toDomain()

        // Then: All fields including nulls are preserved
        assertEquals(originalDiscussion.id, reconvertedDiscussion.id)
        assertNull(reconvertedDiscussion.sessionId)
        assertEquals(originalDiscussion.title, reconvertedDiscussion.title)
        assertEquals(originalDiscussion.date, reconvertedDiscussion.date)
        assertNull(reconvertedDiscussion.location)
    }
}
