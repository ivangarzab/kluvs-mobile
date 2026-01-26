package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.DiscussionEntity
import com.ivangarzab.kluvs.model.Discussion
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DiscussionMappersTest {

    @Test
    fun testDiscussionEntity_toDomain() {
        // Given
        val entity = DiscussionEntity(
            id = "discussion-1",
            sessionId = "session-1",
            title = "Chapter 1-5 Discussion",
            date = "2024-02-01T18:00:00",
            location = "Discord Voice Channel",
            lastFetchedAt = 1234567890L
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("discussion-1", domain.id)
        assertEquals("session-1", domain.sessionId)
        assertEquals("Chapter 1-5 Discussion", domain.title)
        assertEquals(LocalDateTime.parse("2024-02-01T18:00:00"), domain.date)
        assertEquals("Discord Voice Channel", domain.location)
    }

    @Test
    fun testDiscussion_toEntity() {
        // Given
        val domain = Discussion(
            id = "discussion-1",
            sessionId = "session-1",
            title = "Chapter 1-5 Discussion",
            date = LocalDateTime.parse("2024-02-01T18:00:00"),
            location = "Discord Voice Channel"
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("discussion-1", entity.id)
        assertEquals("session-1", entity.sessionId)
        assertEquals("Chapter 1-5 Discussion", entity.title)
        assertEquals("2024-02-01T18:00", entity.date)
        assertEquals("Discord Voice Channel", entity.location)
        assertNotNull(entity.lastFetchedAt)
    }
}
