package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ClubEntityTest {

    @Test
    fun testClubEntity_creation() {
        // Given
        val clubEntity = ClubEntity(
            id = "club-1",
            serverId = "server-1",
            name = "Sci-Fi Book Club",
            discordChannel = "#sci-fi",
            foundedDate = "2024-01-01",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("club-1", clubEntity.id)
        assertEquals("server-1", clubEntity.serverId)
        assertEquals("Sci-Fi Book Club", clubEntity.name)
        assertEquals("#sci-fi", clubEntity.discordChannel)
        assertEquals("2024-01-01", clubEntity.foundedDate)
        assertEquals(1234567890L, clubEntity.lastFetchedAt)
    }

    @Test
    fun testClubEntity_withNullFields() {
        // Given
        val clubEntity = ClubEntity(
            id = "club-1",
            serverId = null,
            name = "Book Club",
            discordChannel = null,
            foundedDate = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("club-1", clubEntity.id)
        assertEquals(null, clubEntity.serverId)
        assertEquals(null, clubEntity.discordChannel)
        assertEquals(null, clubEntity.foundedDate)
    }

    @Test
    fun testClubEntity_copy() {
        // Given
        val original = ClubEntity(
            id = "club-1",
            serverId = "server-1",
            name = "Original Club",
            discordChannel = "#original",
            foundedDate = "2024-01-01",
            lastFetchedAt = 1234567890L
        )

        // When
        val updated = original.copy(
            name = "Updated Club",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("club-1", updated.id)
        assertEquals("server-1", updated.serverId)
        assertEquals("Updated Club", updated.name)
        assertEquals("#original", updated.discordChannel)
        assertEquals("2024-01-01", updated.foundedDate)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testClubEntity_equality() {
        // Given
        val club1 = ClubEntity(
            id = "club-1",
            serverId = "server-1",
            name = "Book Club",
            discordChannel = "#books",
            foundedDate = "2024-01-01",
            lastFetchedAt = 1234567890L
        )

        val club2 = ClubEntity(
            id = "club-1",
            serverId = "server-1",
            name = "Book Club",
            discordChannel = "#books",
            foundedDate = "2024-01-01",
            lastFetchedAt = 1234567890L
        )

        val club3 = ClubEntity(
            id = "club-2",
            serverId = "server-1",
            name = "Book Club",
            discordChannel = "#books",
            foundedDate = "2024-01-01",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(club1, club2)
        assertNotEquals(club1, club3)
    }
}
