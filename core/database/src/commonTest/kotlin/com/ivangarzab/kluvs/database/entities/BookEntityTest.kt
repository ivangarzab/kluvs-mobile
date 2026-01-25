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
            year = "1937",
            pageCount = 310,
            coverUrl = "https://example.com/hobbit.jpg",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("book-1", bookEntity.id)
        assertEquals("The Hobbit", bookEntity.title)
        assertEquals("J.R.R. Tolkien", bookEntity.author)
        assertEquals("1937", bookEntity.year)
        assertEquals(310, bookEntity.pageCount)
        assertEquals("https://example.com/hobbit.jpg", bookEntity.coverUrl)
        assertEquals(1234567890L, bookEntity.lastFetchedAt)
    }

    @Test
    fun testBookEntity_withNullFields() {
        // Given
        val bookEntity = BookEntity(
            id = "book-1",
            title = "Unknown Book",
            author = null,
            year = null,
            pageCount = null,
            coverUrl = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("book-1", bookEntity.id)
        assertEquals("Unknown Book", bookEntity.title)
        assertEquals(null, bookEntity.author)
        assertEquals(null, bookEntity.year)
        assertEquals(null, bookEntity.pageCount)
        assertEquals(null, bookEntity.coverUrl)
    }

    @Test
    fun testBookEntity_copy() {
        // Given
        val original = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            year = "1937",
            pageCount = 310,
            coverUrl = "https://example.com/hobbit.jpg",
            lastFetchedAt = 1234567890L
        )

        // When - update with new cover URL
        val updated = original.copy(
            coverUrl = "https://example.com/hobbit-new.jpg",
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("book-1", updated.id)
        assertEquals("The Hobbit", updated.title)
        assertEquals("J.R.R. Tolkien", updated.author)
        assertEquals("1937", updated.year)
        assertEquals(310, updated.pageCount)
        assertEquals("https://example.com/hobbit-new.jpg", updated.coverUrl)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testBookEntity_equality() {
        // Given
        val book1 = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            year = "1937",
            pageCount = 310,
            coverUrl = "https://example.com/hobbit.jpg",
            lastFetchedAt = 1234567890L
        )

        val book2 = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            year = "1937",
            pageCount = 310,
            coverUrl = "https://example.com/hobbit.jpg",
            lastFetchedAt = 1234567890L
        )

        val book3 = BookEntity(
            id = "book-2",
            title = "1984",
            author = "George Orwell",
            year = "1949",
            pageCount = 328,
            coverUrl = "https://example.com/1984.jpg",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(book1, book2)
        assertNotEquals(book1, book3)
    }
}