package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.local.source.BookLocalDataSource
import com.ivangarzab.kluvs.data.remote.source.BookRemoteDataSource
import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.model.BookSearchResult
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookRepositoryTest {

    private lateinit var remoteDataSource: BookRemoteDataSource
    private lateinit var localDataSource: BookLocalDataSource
    private lateinit var repository: BookRepository

    private val testBook = Book(
        id = "42",
        title = "The Hobbit",
        author = "J.R.R. Tolkien",
        isbn = "978-0-395-07122-1",
        externalGoogleId = "goog-hobbit"
    )

    @BeforeTest
    fun setup() {
        remoteDataSource = mock<BookRemoteDataSource>()
        localDataSource = mock<BookLocalDataSource>()
        repository = BookRepositoryImpl(remoteDataSource, localDataSource)

        everySuspend { localDataSource.insertBook(any()) } returns Unit
    }

    // ========================================
    // SEARCH BOOKS
    // ========================================

    @Test
    fun `searchBooks success returns list of books`() = runTest {
        everySuspend { remoteDataSource.searchBooks(any(), any()) } returns
            Result.success(BookSearchResult(books = listOf(testBook), total = 1))

        val result = repository.searchBooks("hobbit")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.books?.size)
        assertEquals("The Hobbit", result.getOrNull()?.books?.first()?.title)
        assertEquals(1, result.getOrNull()?.total)
        verifySuspend { remoteDataSource.searchBooks("hobbit", 0) }
    }

    @Test
    fun `searchBooks returns empty list when no results`() = runTest {
        everySuspend { remoteDataSource.searchBooks(any(), any()) } returns
            Result.success(BookSearchResult(books = emptyList(), total = 0))

        val result = repository.searchBooks("xyzzy")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.books?.size)
    }

    @Test
    fun `searchBooks passes offset through to the remote data source`() = runTest {
        everySuspend { remoteDataSource.searchBooks(any(), any()) } returns
            Result.success(BookSearchResult(books = listOf(testBook), total = 25))

        repository.searchBooks("hobbit", offset = 10)

        verifySuspend { remoteDataSource.searchBooks("hobbit", 10) }
    }

    @Test
    fun `searchBooks failure returns Result failure`() = runTest {
        val exception = Exception("Network error")
        everySuspend { remoteDataSource.searchBooks(any(), any()) } returns Result.failure(exception)

        val result = repository.searchBooks("hobbit")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `searchBooks does not cache results`() = runTest {
        everySuspend { remoteDataSource.searchBooks(any(), any()) } returns
            Result.success(BookSearchResult(books = listOf(testBook), total = 1))

        repository.searchBooks("hobbit")

        verifySuspend(mode = dev.mokkery.verify.VerifyMode.not) { localDataSource.insertBook(any()) }
    }

    // ========================================
    // REGISTER BOOK
    // ========================================

    @Test
    fun `registerBook success caches and returns book`() = runTest {
        everySuspend { remoteDataSource.registerBook(any()) } returns Result.success(testBook)

        val result = repository.registerBook(testBook)

        assertTrue(result.isSuccess)
        assertEquals("42", result.getOrNull()?.id)
        assertEquals("The Hobbit", result.getOrNull()?.title)
        verifySuspend { remoteDataSource.registerBook(testBook) }
        verifySuspend { localDataSource.insertBook(testBook) }
    }

    @Test
    fun `registerBook failure returns Result failure and does not cache`() = runTest {
        val exception = Exception("Server error")
        everySuspend { remoteDataSource.registerBook(any()) } returns Result.failure(exception)

        val result = repository.registerBook(testBook)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend(mode = dev.mokkery.verify.VerifyMode.not) { localDataSource.insertBook(any()) }
    }
}
