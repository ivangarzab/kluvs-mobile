package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.DatabaseMockFixture
import com.ivangarzab.kluvs.database.entities.BookEntity
import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.model.Book
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BookLocalDataSourceTest {

    private lateinit var fixture: DatabaseMockFixture
    private lateinit var dataSource: BookLocalDataSource

    private fun setup() {
        fixture = DatabaseMockFixture()
        dataSource = BookLocalDataSourceImpl(fixture.database)
    }

    @Test
    fun `getBook returns book when it exists`() = runTest {
        setup()
        val bookId = "book-1"
        val entity = BookEntity(bookId, "The Hobbit", "Tolkien", null, 1937, null, null, null, null, 0)
        everySuspend { fixture.bookDao.getBook(bookId) } returns entity

        val result = dataSource.getBook(bookId)

        assertEquals(entity.toDomain(), result)
    }

    @Test
    fun `getBook returns null when book does not exist`() = runTest {
        setup()
        everySuspend { fixture.bookDao.getBook("not-found") } returns null

        assertNull(dataSource.getBook("not-found"))
    }

    @Test
    fun `getAllBooks returns all books`() = runTest {
        setup()
        val books = listOf(
            BookEntity("book-1", "The Hobbit", "Tolkien", null, 1937, null, null, null, null, 0),
            BookEntity("book-2", "Dune", "Herbert", null, 1965, null, null, null, null, 0)
        )
        everySuspend { fixture.bookDao.getAllBooks() } returns books

        val result = dataSource.getAllBooks()

        assertEquals(books.map { it.toDomain() }, result)
    }

    @Test
    fun `insertBook inserts single book`() = runTest {
        setup()
        val book = Book("book-1", "The Hobbit", "Tolkien", null, 1937, null)
        everySuspend { fixture.bookDao.insertBook(book.toEntity()) } returns Unit

        dataSource.insertBook(book)
    }

    @Test
    fun `deleteBook deletes existing book`() = runTest {
        setup()
        val entity = BookEntity("book-1", "The Hobbit", "Tolkien", null, 1937, null, null, null, null, 0)
        everySuspend { fixture.bookDao.getBook("book-1") } returns entity
        everySuspend { fixture.bookDao.deleteBook(entity) } returns Unit

        dataSource.deleteBook("book-1")
    }

    @Test
    fun `deleteAll clears all books`() = runTest {
        setup()

        dataSource.deleteAll()
    }

    private fun Book.toEntity() = BookEntity(
        id = id,
        title = title,
        author = author,
        edition = edition,
        year = year,
        isbn = isbn,
        pageCount = null,
        imageUrl = null,
        externalGoogleId = null,
        lastFetchedAt = 0
    )
}
