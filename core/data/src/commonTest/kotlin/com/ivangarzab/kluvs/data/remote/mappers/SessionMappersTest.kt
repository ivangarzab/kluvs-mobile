package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.data.remote.dtos.ClubDto
import com.ivangarzab.kluvs.data.remote.dtos.DiscussionDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionResponseDto
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionMappersTest {

    @Test
    fun `SessionDto toDomain maps all fields correctly`() {
        // Given: A SessionDto with full data
        val bookDto = BookDto(
            id = "book-1",
            title = "Test Book",
            author = "Test Author",
            edition = "1st",
            year = 2024,
            isbn = "123-456"
        )

        val discussionDto = DiscussionDto(
            id = "disc-1",
            session_id = "session-1",
            title = "Chapter 1",
            date = "2024-06-15T18:00:00",
            location = "Discord"
        )

        val dto = SessionDto(
            id = "session-1",
            club_id = "club-1",
            book = bookDto,
            due_date = "2024-12-31T23:59:59",
            discussions = listOf(discussionDto)
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: All fields are mapped correctly
        assertEquals("session-1", domain.id)
        assertEquals("club-1", domain.clubId)
        assertEquals("Test Book", domain.book.title)
        assertEquals(LocalDateTime(2024, 12, 31, 23, 59, 59), domain.dueDate)
        assertEquals(1, domain.discussions.size)
        assertEquals("Chapter 1", domain.discussions.first().title)
    }

    @Test
    fun `SessionDto toDomain handles nullable club_id`() {
        // Given: A SessionDto with null club_id
        val bookDto = BookDto(
            id = "book-1",
            title = "Book",
            author = "Author"
        )

        val dto = SessionDto(
            id = "session-2",
            club_id = null,
            book = bookDto,
            due_date = null,
            discussions = emptyList()
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: club_id defaults to empty string
        assertEquals("session-2", domain.id)
        assertEquals("", domain.clubId)
        assertNull(domain.dueDate)
        assertTrue(domain.discussions.isEmpty())
    }

    @Test
    fun `SessionResponseDto toDomain maps nested club correctly`() {
        // Given: A SessionResponseDto with nested ClubDto
        val clubDto = ClubDto(
            id = "club-1",
            name = "My Club",
            discord_channel = "123456789",
            server_id = "987654321"
        )

        val bookDto = BookDto(
            id = "book-1",
            title = "Response Book",
            author = "Response Author"
        )

        val dto = SessionResponseDto(
            id = "session-3",
            club = clubDto,
            book = bookDto,
            due_date = "2024-06-30T00:00:00",
            shame_list = emptyList(),
            discussions = emptyList()
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: ClubId is extracted from nested club
        assertEquals("session-3", domain.id)
        assertEquals("club-1", domain.clubId)
        assertEquals("Response Book", domain.book.title)
        assertEquals(LocalDateTime(2024, 6, 30, 0, 0, 0), domain.dueDate)
    }

    @Test
    fun `SessionResponseDto toDomain handles null due_date`() {
        // Given: A SessionResponseDto with no due date
        val clubDto = ClubDto(
            id = "club-2",
            name = "No Deadline Club",
            discord_channel = null,
            server_id = null
        )

        val bookDto = BookDto(
            id = "book-2",
            title = "Timeless",
            author = "Forever"
        )

        val dto = SessionResponseDto(
            id = "session-4",
            club = clubDto,
            book = bookDto,
            due_date = null,
            shame_list = emptyList(),
            discussions = emptyList()
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: dueDate is null
        assertEquals("session-4", domain.id)
        assertNull(domain.dueDate)
    }

    @Test
    fun `SessionDto toDomain handles empty discussions list`() {
        // Given: A SessionDto with no discussions
        val bookDto = BookDto(
            id = "book-3",
            title = "Solo Book",
            author = "Lonely Author"
        )

        val dto = SessionDto(
            id = "session-5",
            club_id = "club-3",
            book = bookDto,
            due_date = null,
            discussions = emptyList()
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: discussions is empty
        assertTrue(domain.discussions.isEmpty())
    }

    @Test
    fun `SessionDto toDomain maps multiple discussions`() {
        // Given: A SessionDto with multiple discussions
        val bookDto = BookDto(id = "book-4", title = "Multi", author = "Discuss")

        val discussions = listOf(
            DiscussionDto("disc-1", "session-6", "Part 1", "2024-01-15T10:00:00", "Online"),
            DiscussionDto("disc-2", "session-6", "Part 2", "2024-01-22T10:00:00", "Online"),
            DiscussionDto("disc-3", "session-6", "Finale", "2024-01-29T10:00:00", "In-person")
        )

        val dto = SessionDto(
            id = "session-6",
            club_id = "club-4",
            book = bookDto,
            due_date = "2024-02-01T00:00:00",
            discussions = discussions
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: All discussions are mapped
        assertEquals(3, domain.discussions.size)
        assertEquals("Part 1", domain.discussions[0].title)
        assertEquals("Part 2", domain.discussions[1].title)
        assertEquals("Finale", domain.discussions[2].title)
    }
}
