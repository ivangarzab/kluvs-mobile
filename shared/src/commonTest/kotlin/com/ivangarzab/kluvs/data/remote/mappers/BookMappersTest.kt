package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.util.BarkTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BookMappersTest : BarkTest() {

    @Test
    fun `toDomain maps all fields correctly`() {
        // Given: A BookDto with all fields populated
        val dto = BookDto(
            id = "123",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            edition = "First Edition",
            year = 1937,
            isbn = "978-0-395-07122-1",
            page_count = 310
        )

        // When: Mapping to domain
        val domain = dto.toDomain()
        Bark.v("Domain BookDto: $domain")

        // Then: All fields are mapped correctly
        assertEquals("123", domain.id)
        assertEquals("The Hobbit", domain.title)
        assertEquals("J.R.R. Tolkien", domain.author)
        assertEquals("First Edition", domain.edition)
        assertEquals(1937, domain.year)
        assertEquals("978-0-395-07122-1", domain.isbn)
        assertEquals(310, domain.pageCount)
    }

    @Test
    fun `toDomain handles nullable fields correctly`() {
        // Given: A BookDto with only required fields
        val dto = BookDto(
            id = null,
            title = "Some Book",
            author = "Some Author",
            edition = null,
            year = null,
            isbn = null,
            page_count = null
        )

        // When: Mapping to domain
        val domain = dto.toDomain()
        Bark.v("Domain BookDto: $domain")

        // Then: Nullable fields are null
        assertNull(domain.id)
        assertEquals("Some Book", domain.title)
        assertEquals("Some Author", domain.author)
        assertNull(domain.edition)
        assertNull(domain.year)
        assertNull(domain.isbn)
        assertNull(domain.pageCount)
    }
}
