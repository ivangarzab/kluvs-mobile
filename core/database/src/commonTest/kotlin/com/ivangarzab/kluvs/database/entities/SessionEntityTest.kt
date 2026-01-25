package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SessionEntityTest {

    @Test
    fun testSessionEntity_creation() {
        // Given
        val sessionEntity = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            dueDate = "2024-02-01T12:00:00Z",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("session-1", sessionEntity.id)
        assertEquals("club-1", sessionEntity.clubId)
        assertEquals("book-1", sessionEntity.bookId)
        assertEquals("2024-02-01T12:00:00Z", sessionEntity.dueDate)
        assertEquals(1234567890L, sessionEntity.lastFetchedAt)
    }

    @Test
    fun testSessionEntity_withNullFields() {
        // Given
        val sessionEntity = SessionEntity(
            id = "session-1",
            clubId = null,
            bookId = null,
            dueDate = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("session-1", sessionEntity.id)
        assertEquals(null, sessionEntity.clubId)
        assertEquals(null, sessionEntity.bookId)
        assertEquals(null, sessionEntity.dueDate)
    }

    @Test
    fun testSessionEntity_copy() {
        // Given
        val original = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            dueDate = "2024-02-01T12:00:00Z",
            lastFetchedAt = 1234567890L
        )

        // When - update due date
        val updated = original.copy(
            dueDate = "2024-02-15T12:00:00Z",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("session-1", updated.id)
        assertEquals("club-1", updated.clubId)
        assertEquals("book-1", updated.bookId)
        assertEquals("2024-02-15T12:00:00Z", updated.dueDate)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testSessionEntity_equality() {
        // Given
        val session1 = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            dueDate = "2024-02-01T12:00:00Z",
            lastFetchedAt = 1234567890L
        )

        val session2 = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            dueDate = "2024-02-01T12:00:00Z",
            lastFetchedAt = 1234567890L
        )

        val session3 = SessionEntity(
            id = "session-2",
            clubId = "club-1",
            bookId = "book-2",
            dueDate = "2024-03-01T12:00:00Z",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(session1, session2)
        assertNotEquals(session1, session3)
    }
}
