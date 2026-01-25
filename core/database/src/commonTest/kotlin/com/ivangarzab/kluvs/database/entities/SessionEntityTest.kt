package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SessionEntityTest {

    @Test
    fun testSessionEntity_creation() {
        // Given
        val sessionEntity = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            startDate = "2024-01-01",
            endDate = "2024-02-01",
            isActive = true,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("session-1", sessionEntity.id)
        assertEquals("club-1", sessionEntity.clubId)
        assertEquals("book-1", sessionEntity.bookId)
        assertEquals("2024-01-01", sessionEntity.startDate)
        assertEquals("2024-02-01", sessionEntity.endDate)
        assertTrue(sessionEntity.isActive)
        assertEquals(1234567890L, sessionEntity.lastFetchedAt)
    }

    @Test
    fun testSessionEntity_withNullFields() {
        // Given
        val sessionEntity = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = null,
            startDate = null,
            endDate = null,
            isActive = false,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("session-1", sessionEntity.id)
        assertEquals(null, sessionEntity.bookId)
        assertEquals(null, sessionEntity.startDate)
        assertEquals(null, sessionEntity.endDate)
        assertFalse(sessionEntity.isActive)
    }

    @Test
    fun testSessionEntity_copy() {
        // Given
        val original = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            startDate = "2024-01-01",
            endDate = "2024-02-01",
            isActive = true,
            lastFetchedAt = 1234567890L
        )

        // When - mark session as inactive
        val updated = original.copy(
            isActive = false,
            endDate = "2024-01-15",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("session-1", updated.id)
        assertEquals("club-1", updated.clubId)
        assertEquals("book-1", updated.bookId)
        assertEquals("2024-01-01", updated.startDate)
        assertEquals("2024-01-15", updated.endDate)
        assertFalse(updated.isActive)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testSessionEntity_equality() {
        // Given
        val session1 = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            startDate = "2024-01-01",
            endDate = "2024-02-01",
            isActive = true,
            lastFetchedAt = 1234567890L
        )

        val session2 = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            startDate = "2024-01-01",
            endDate = "2024-02-01",
            isActive = true,
            lastFetchedAt = 1234567890L
        )

        val session3 = SessionEntity(
            id = "session-2",
            clubId = "club-1",
            bookId = "book-2",
            startDate = "2024-02-01",
            endDate = "2024-03-01",
            isActive = false,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(session1, session2)
        assertNotEquals(session1, session3)
    }
}
