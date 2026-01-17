package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerClubDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ServerMappersTest {

    @Test
    fun `ServerDto toDomain maps basic fields only`() {
        // Given: A ServerDto
        val dto = ServerDto(
            id = "server-1",
            name = "Production Server"
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: Basic fields are mapped, clubs are null
        assertEquals("server-1", domain.id)
        assertEquals("Production Server", domain.name)
        assertNull(domain.clubs)
    }

    @Test
    fun `ServerResponseDto toDomain maps nested clubs`() {
        // Given: A ServerResponseDto with nested clubs
        val sessionDto = SessionDto(
            id = "session-1",
            club_id = "club-1",
            book = BookDto(id = "book-1", title = "Book", author = "Author"),
            due_date = null,
            discussions = emptyList()
        )

        val clubDto1 = ServerClubDto(
            id = "club-1",
            name = "Club One",
            discord_channel = "123456789",
            member_count = 10,
            latest_session = sessionDto
        )

        val clubDto2 = ServerClubDto(
            id = "club-2",
            name = "Club Two",
            discord_channel = "987654321",
            member_count = 5,
            latest_session = null
        )

        val dto = ServerResponseDto(
            id = "server-2",
            name = "Test Server",
            clubs = listOf(clubDto1, clubDto2)
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: All nested clubs are mapped
        assertEquals("server-2", domain.id)
        assertEquals("Test Server", domain.name)
        assertNotNull(domain.clubs)
        assertEquals(2, domain.clubs?.size)

        val firstClub = domain.clubs?.first()
        assertEquals("club-1", firstClub?.id)
        assertEquals("Club One", firstClub?.name)
        assertNotNull(firstClub?.activeSession)
        assertEquals("session-1", firstClub?.activeSession?.id)

        val secondClub = domain.clubs?.get(1)
        assertEquals("club-2", secondClub?.id)
        assertNull(secondClub?.activeSession)
    }

    @Test
    fun `ServerResponseDto toDomain handles empty clubs list`() {
        // Given: A ServerResponseDto with no clubs
        val dto = ServerResponseDto(
            id = "server-3",
            name = "Empty Server",
            clubs = emptyList()
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: clubs is an empty list (not null)
        assertEquals("server-3", domain.id)
        assertEquals("Empty Server", domain.name)
        assertNotNull(domain.clubs)
        assertTrue(domain.clubs?.isEmpty() == true)
    }

    @Test
    fun `ServerResponseDto toDomain handles clubs with varying data`() {
        // Given: Clubs with different levels of completeness
        val fullClub = ServerClubDto(
            id = "club-full",
            name = "Full Club",
            discord_channel = "111222333",
            member_count = 20,
            latest_session = SessionDto(
                id = "session-full",
                club_id = "club-full",
                book = BookDto(id = "book-full", title = "Full Book", author = "Full Author"),
                due_date = "2024-12-31T00:00:00",
                discussions = emptyList()
            )
        )

        val minimalClub = ServerClubDto(
            id = "club-minimal",
            name = "Minimal Club",
            discord_channel = null,
            member_count = null,
            latest_session = null
        )

        val dto = ServerResponseDto(
            id = "server-4",
            name = "Mixed Server",
            clubs = listOf(fullClub, minimalClub)
        )

        // When: Mapping to domain
        val domain = dto.toDomain()

        // Then: Both clubs are mapped correctly
        assertEquals(2, domain.clubs?.size)

        val full = domain.clubs?.first { it.id == "club-full" }
        assertEquals("Full Club", full?.name)
        assertNotNull(full?.activeSession)

        val minimal = domain.clubs?.first { it.id == "club-minimal" }
        assertEquals("Minimal Club", minimal?.name)
        assertNull(minimal?.activeSession)
        assertNull(minimal?.discordChannel)
    }
}
