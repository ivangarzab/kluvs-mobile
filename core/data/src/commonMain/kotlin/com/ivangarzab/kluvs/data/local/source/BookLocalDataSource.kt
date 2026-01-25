package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.model.Book

/**
 * Local data source for Book entities.
 * Handles CRUD operations with the local Room database.
 */
interface BookLocalDataSource {
    suspend fun getBook(bookId: String): Book?
    suspend fun getAllBooks(): List<Book>
    suspend fun insertBook(book: Book)
    suspend fun insertBooks(books: List<Book>)
    suspend fun deleteBook(bookId: String)
    suspend fun getLastFetchedAt(bookId: String): Long?
    suspend fun deleteAll()
}

/**
 * Implementation of [BookLocalDataSource] using Room database.
 */
class BookLocalDataSourceImpl(
    private val database: KluvsDatabase
) : BookLocalDataSource {

    private val bookDao = database.bookDao()

    override suspend fun getBook(bookId: String): Book? {
        return bookDao.getBook(bookId)?.toDomain()
    }

    override suspend fun getAllBooks(): List<Book> {
        return bookDao.getAllBooks().map { it.toDomain() }
    }

    override suspend fun insertBook(book: Book) {
        bookDao.insertBook(book.toEntity())
    }

    override suspend fun insertBooks(books: List<Book>) {
        bookDao.insertBooks(books.map { it.toEntity() })
    }

    override suspend fun deleteBook(bookId: String) {
        val entity = bookDao.getBook(bookId)
        if (entity != null) {
            bookDao.deleteBook(entity)
        }
    }

    override suspend fun getLastFetchedAt(bookId: String): Long? {
        return bookDao.getLastFetchedAt(bookId)
    }

    override suspend fun deleteAll() {
        bookDao.deleteAll()
    }
}
