package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ServerEntityTest {

    @Test
    fun testServerEntity_creation() {
        // Given
        val serverEntity = ServerEntity(
            id = "server-1",
            name = "Book Lovers Discord",
            iconUrl = "https://example.com/icon.png",
            memberCount = 150,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("server-1", serverEntity.id)
        assertEquals("Book Lovers Discord", serverEntity.name)
        assertEquals("https://example.com/icon.png", serverEntity.iconUrl)
        assertEquals(150, serverEntity.memberCount)
        assertEquals(1234567890L, serverEntity.lastFetchedAt)
    }

    @Test
    fun testServerEntity_withNullIcon() {
        // Given
        val serverEntity = ServerEntity(
            id = "server-1",
            name = "Book Lovers Discord",
            iconUrl = null,
            memberCount = 100,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("server-1", serverEntity.id)
        assertEquals(null, serverEntity.iconUrl)
    }

    @Test
    fun testServerEntity_copy() {
        // Given
        val original = ServerEntity(
            id = "server-1",
            name = "Original Server",
            iconUrl = "https://example.com/icon.png",
            memberCount = 100,
            lastFetchedAt = 1234567890L
        )

        // When
        val updated = original.copy(
            memberCount = 200,
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("server-1", updated.id)
        assertEquals("Original Server", updated.name)
        assertEquals("https://example.com/icon.png", updated.iconUrl)
        assertEquals(200, updated.memberCount)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testServerEntity_equality() {
        // Given
        val server1 = ServerEntity(
            id = "server-1",
            name = "Book Club",
            iconUrl = "https://example.com/icon.png",
            memberCount = 100,
            lastFetchedAt = 1234567890L
        )

        val server2 = ServerEntity(
            id = "server-1",
            name = "Book Club",
            iconUrl = "https://example.com/icon.png",
            memberCount = 100,
            lastFetchedAt = 1234567890L
        )

        val server3 = ServerEntity(
            id = "server-2",
            name = "Different Club",
            iconUrl = null,
            memberCount = 50,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(server1, server2)
        assertNotEquals(server1, server3)
    }
}