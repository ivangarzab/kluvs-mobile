package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.ServerEntity
import com.ivangarzab.kluvs.model.Server
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ServerMappersTest {

    @Test
    fun testServerEntity_toDomain() {
        // Given
        val entity = ServerEntity(
            id = "server-1",
            name = "Book Lovers Discord",
            lastFetchedAt = 1234567890L
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("server-1", domain.id)
        assertEquals("Book Lovers Discord", domain.name)
        assertNull(domain.clubs) // Relationship not loaded
    }

    @Test
    fun testServer_toEntity() {
        // Given
        val domain = Server(
            id = "server-1",
            name = "Book Lovers Discord",
            clubs = null
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("server-1", entity.id)
        assertEquals("Book Lovers Discord", entity.name)
        assertNotNull(entity.lastFetchedAt)
    }
}
