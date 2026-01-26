package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.SessionEntity
import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.model.Session
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SessionMappersTest {

    @Test
    fun testSessionEntity_toDomain() {
        // Given
        val entity = SessionEntity(
            id = "session-1",
            clubId = "club-1",
            bookId = "book-1",
            dueDate = "2024-02-01T12:00:00",
            lastFetchedAt = 1234567890L
        )
        val book = Book(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            isbn = null
        )

        // When
        val domain = entity.toDomain(book)

        // Then
        assertEquals("session-1", domain.id)
        assertEquals("club-1", domain.clubId)
        assertEquals(book, domain.book)
        assertEquals(LocalDateTime.parse("2024-02-01T12:00:00"), domain.dueDate)
        assertEquals(emptyList(), domain.discussions)
    }

    @Test
    fun testSession_toEntity() {
        // Given
        val domain = Session(
            id = "session-1",
            clubId = "club-1",
            book = Book(
                id = "book-1",
                title = "The Hobbit",
                author = "J.R.R. Tolkien",
                isbn = null
            ),
            dueDate = LocalDateTime.parse("2024-02-01T12:00:00")
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("session-1", entity.id)
        assertEquals("club-1", entity.clubId)
        assertEquals("book-1", entity.bookId)
        assertEquals("2024-02-01T12:00", entity.dueDate)
        assertNotNull(entity.lastFetchedAt)
    }
}
