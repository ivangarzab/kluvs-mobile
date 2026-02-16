package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.local.source.BookLocalDataSource
import com.ivangarzab.kluvs.data.remote.source.BookRemoteDataSource
import com.ivangarzab.kluvs.model.Book

/**
 * Repository for Book data.
 *
 * Provides:
 * - Book search via Google Books (remote only, not cached)
 * - Book registration — creates or retrieves a server-side Book record,
 *   then caches the result locally
 */
interface BookRepository {

    /**
     * Searches for books matching the given query string.
     *
     * Results come directly from the remote API and are not cached.
     *
     * @param query Free-text search query
     * @return Result containing a list of matching [Book]s, or an error
     */
    suspend fun searchBooks(query: String): Result<List<Book>>

    /**
     * Registers a book with the backend (creates if not exists, returns existing otherwise).
     *
     * The returned [Book] will have a server-assigned [Book.id] that can be used
     * as a `book_id` when creating sessions.
     *
     * @param book The book to register
     * @return Result containing the registered [Book], or an error
     */
    suspend fun registerBook(book: Book): Result<Book>
}

internal class BookRepositoryImpl(
    private val bookRemoteDataSource: BookRemoteDataSource,
    private val bookLocalDataSource: BookLocalDataSource
) : BookRepository {

    override suspend fun searchBooks(query: String): Result<List<Book>> {
        Bark.d("Searching books (query: \"$query\")")
        return bookRemoteDataSource.searchBooks(query)
            .onSuccess { books ->
                Bark.i("Book search complete (${books.size} results)")
            }.onFailure { error ->
                Bark.e("Book search failed. Check network and retry.", error)
            }
    }

    override suspend fun registerBook(book: Book): Result<Book> {
        Bark.d("Registering book: ${book.title}")
        val result = bookRemoteDataSource.registerBook(book)

        result.onSuccess { registered ->
            Bark.v("Caching registered book (ID: ${registered.id})")
            try {
                bookLocalDataSource.insertBook(registered)
                Bark.i("Book registered and cached (ID: ${registered.id})")
            } catch (e: Exception) {
                Bark.e("Book cache failed after registration. Remote data still valid.", e)
            }
        }.onFailure { error ->
            Bark.e("Book registration failed. Check input and retry.", error)
        }

        return result
    }
}
