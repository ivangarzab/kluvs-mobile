package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.ClubEntity
import com.ivangarzab.kluvs.model.Club
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ClubMappersTest {

    @Test
    fun testClubEntity_toDomain() {
        // Given
        val entity = ClubEntity(
            id = "club-1",
            serverId = "server-1",
            name = "Sci-Fi Book Club",
            discordChannel = "#sci-fi",
            foundedDate = "2024-01-01",
            lastFetchedAt = 1234567890L
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("club-1", domain.id)
        assertEquals("server-1", domain.serverId)
        assertEquals("Sci-Fi Book Club", domain.name)
        assertEquals("#sci-fi", domain.discordChannel)
        assertEquals(LocalDate.parse("2024-01-01"), domain.foundedDate)
        assertEquals(emptyList(), domain.shameList)
        assertNull(domain.members) // Relationship not loaded
        assertNull(domain.activeSession) // Relationship not loaded
        assertNull(domain.pastSessions) // Relationship not loaded
    }

    @Test
    fun testClubEntity_toDomain_withNullFields() {
        // Given
        val entity = ClubEntity(
            id = "club-1",
            serverId = null,
            name = "Book Club",
            discordChannel = null,
            foundedDate = null,
            lastFetchedAt = 1234567890L
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("club-1", domain.id)
        assertNull(domain.serverId)
        assertNull(domain.discordChannel)
        assertNull(domain.foundedDate)
    }

    @Test
    fun testClub_toEntity() {
        // Given
        val domain = Club(
            id = "club-1",
            name = "Sci-Fi Book Club",
            discordChannel = "#sci-fi",
            serverId = "server-1",
            foundedDate = LocalDate.parse("2024-01-01")
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("club-1", entity.id)
        assertEquals("server-1", entity.serverId)
        assertEquals("Sci-Fi Book Club", entity.name)
        assertEquals("#sci-fi", entity.discordChannel)
        assertEquals("2024-01-01", entity.foundedDate)
        assertNotNull(entity.lastFetchedAt)
    }
}
