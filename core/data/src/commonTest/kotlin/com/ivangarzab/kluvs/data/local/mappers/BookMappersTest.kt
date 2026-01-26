package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.BookEntity
import com.ivangarzab.kluvs.model.Book
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BookMappersTest {

    @Test
    fun testBookEntity_toDomain() {
        // Given
        val entity = BookEntity(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0547928227",
            pageCount = 310,
            lastFetchedAt = 1234567890L
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("book-1", domain.id)
        assertEquals("The Hobbit", domain.title)
        assertEquals("J.R.R. Tolkien", domain.author)
        assertEquals("First Edition", domain.edition)
        assertEquals(1937, domain.year)
        assertEquals("978-0547928227", domain.isbn)
        assertEquals(310, domain.pageCount)
    }

    @Test
    fun testBook_toEntity() {
        // Given
        val domain = Book(
            id = "book-1",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0547928227",
            pageCount = 310
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("book-1", entity.id)
        assertEquals("The Hobbit", entity.title)
        assertEquals("J.R.R. Tolkien", entity.author)
        assertEquals("First Edition", entity.edition)
        assertEquals(1937, entity.year)
        assertEquals("978-0547928227", entity.isbn)
        assertEquals(310, entity.pageCount)
        // lastFetchedAt should be set to current time
        assertNotNull(entity.lastFetchedAt)
    }

    @Test
    fun testBookEntity_toDomain_withNullFields() {
        // Given
        val entity = BookEntity(
            id = "book-1",
            title = "Unknown Book",
            author = "Unknown Author",
            edition = null,
            year = null,
            isbn = null,
            pageCount = null,
            lastFetchedAt = 1234567890L
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("book-1", domain.id)
        assertEquals("Unknown Book", domain.title)
        assertEquals("Unknown Author", domain.author)
        assertEquals(null, domain.edition)
        assertEquals(null, domain.year)
        assertEquals(null, domain.isbn)
        assertEquals(null, domain.pageCount)
    }
}
