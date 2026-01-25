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
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("server-1", serverEntity.id)
        assertEquals("Book Lovers Discord", serverEntity.name)
        assertEquals(1234567890L, serverEntity.lastFetchedAt)
    }

    @Test
    fun testServerEntity_copy() {
        // Given
        val original = ServerEntity(
            id = "server-1",
            name = "Original Server",
            lastFetchedAt = 1234567890L
        )

        // When
        val updated = original.copy(
            name = "Updated Server",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("server-1", updated.id)
        assertEquals("Updated Server", updated.name)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testServerEntity_equality() {
        // Given
        val server1 = ServerEntity(
            id = "server-1",
            name = "Book Club",
            lastFetchedAt = 1234567890L
        )

        val server2 = ServerEntity(
            id = "server-1",
            name = "Book Club",
            lastFetchedAt = 1234567890L
        )

        val server3 = ServerEntity(
            id = "server-2",
            name = "Different Club",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(server1, server2)
        assertNotEquals(server1, server3)
    }
}