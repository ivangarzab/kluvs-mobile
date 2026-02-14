package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BookEntityTest {

    @Test
    fun testBookEntity_creation() {
        // Given
        val bookEntity = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0547928227",
            pageCount = 310,
            imageUrl = "https://example.com/hobbit.jpg",
            externalGoogleId = "goog-123",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("book-1", bookEntity.id)
        assertEquals("The Hobbit", bookEntity.title)
        assertEquals("J.R.R. Tolkien", bookEntity.author)
        assertEquals("First Edition", bookEntity.edition)
        assertEquals(1937, bookEntity.year)
        assertEquals("978-0547928227", bookEntity.isbn)
        assertEquals(310, bookEntity.pageCount)
        assertEquals("https://example.com/hobbit.jpg", bookEntity.imageUrl)
        assertEquals("goog-123", bookEntity.externalGoogleId)
        assertEquals(1234567890L, bookEntity.lastFetchedAt)
    }

    @Test
    fun testBookEntity_withNullFields() {
        // Given
        val bookEntity = BookEntity(
            id = "book-1",
            title = "Unknown Book",
            author = "Unknown Author",
            edition = null,
            year = null,
            isbn = null,
            pageCount = null,
            imageUrl = null,
            externalGoogleId = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("book-1", bookEntity.id)
        assertEquals("Unknown Book", bookEntity.title)
        assertEquals("Unknown Author", bookEntity.author)
        assertEquals(null, bookEntity.edition)
        assertEquals(null, bookEntity.year)
        assertEquals(null, bookEntity.isbn)
        assertEquals(null, bookEntity.pageCount)
        assertEquals(null, bookEntity.imageUrl)
        assertEquals(null, bookEntity.externalGoogleId)
    }

    @Test
    fun testBookEntity_copy() {
        // Given
        val original = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0547928227",
            pageCount = 310,
            imageUrl = null,
            externalGoogleId = null,
            lastFetchedAt = 1234567890L
        )

        // When - update with new edition
        val updated = original.copy(
            edition = "Second Edition",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("book-1", updated.id)
        assertEquals("The Hobbit", updated.title)
        assertEquals("J.R.R. Tolkien", updated.author)
        assertEquals("Second Edition", updated.edition)
        assertEquals(1937, updated.year)
        assertEquals("978-0547928227", updated.isbn)
        assertEquals(310, updated.pageCount)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testBookEntity_equality() {
        // Given
        val book1 = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0547928227",
            pageCount = 310,
            imageUrl = null,
            externalGoogleId = null,
            lastFetchedAt = 1234567890L
        )

        val book2 = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0547928227",
            pageCount = 310,
            imageUrl = null,
            externalGoogleId = null,
            lastFetchedAt = 1234567890L
        )

        val book3 = BookEntity(
            id = "book-2",
            title = "1984",
            author = "George Orwell",
            edition = null,
            year = 1949,
            isbn = "978-0451524935",
            pageCount = 328,
            imageUrl = null,
            externalGoogleId = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(book1, book2)
        assertNotEquals(book1, book3)
    }
}
