package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DiscussionEntityTest {

    @Test
    fun testDiscussionEntity_creation() {
        // Given
        val discussionEntity = DiscussionEntity(
            id = "discussion-1",
            sessionId = "session-1",
            title = "Chapter 1-5 Discussion",
            date = "2024-02-01T18:00:00Z",
            location = "Discord Voice Channel",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("discussion-1", discussionEntity.id)
        assertEquals("session-1", discussionEntity.sessionId)
        assertEquals("Chapter 1-5 Discussion", discussionEntity.title)
        assertEquals("2024-02-01T18:00:00Z", discussionEntity.date)
        assertEquals("Discord Voice Channel", discussionEntity.location)
        assertEquals(1234567890L, discussionEntity.lastFetchedAt)
    }

    @Test
    fun testDiscussionEntity_withNullFields() {
        // Given
        val discussionEntity = DiscussionEntity(
            id = "discussion-1",
            sessionId = null,
            title = "Discussion",
            date = "2024-02-01T18:00:00Z",
            location = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("discussion-1", discussionEntity.id)
        assertEquals(null, discussionEntity.sessionId)
        assertEquals(null, discussionEntity.location)
    }

    @Test
    fun testDiscussionEntity_copy() {
        // Given
        val original = DiscussionEntity(
            id = "discussion-1",
            sessionId = "session-1",
            title = "Chapter 1-5 Discussion",
            date = "2024-02-01T18:00:00Z",
            location = "Discord Voice Channel",
            lastFetchedAt = 1234567890L
        )

        // When - update discussion date
        val updated = original.copy(
            date = "2024-02-08T18:00:00Z",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("discussion-1", updated.id)
        assertEquals("session-1", updated.sessionId)
        assertEquals("Chapter 1-5 Discussion", updated.title)
        assertEquals("2024-02-08T18:00:00Z", updated.date)
        assertEquals("Discord Voice Channel", updated.location)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testDiscussionEntity_equality() {
        // Given
        val discussion1 = DiscussionEntity(
            id = "discussion-1",
            sessionId = "session-1",
            title = "Chapter 1-5 Discussion",
            date = "2024-02-01T18:00:00Z",
            location = "Discord Voice Channel",
            lastFetchedAt = 1234567890L
        )

        val discussion2 = DiscussionEntity(
            id = "discussion-1",
            sessionId = "session-1",
            title = "Chapter 1-5 Discussion",
            date = "2024-02-01T18:00:00Z",
            location = "Discord Voice Channel",
            lastFetchedAt = 1234567890L
        )

        val discussion3 = DiscussionEntity(
            id = "discussion-2",
            sessionId = "session-1",
            title = "Chapter 6-10 Discussion",
            date = "2024-02-15T18:00:00Z",
            location = "Discord Voice Channel",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(discussion1, discussion2)
        assertNotEquals(discussion1, discussion3)
    }
}
