package com.ivangarzab.kluvs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivangarzab.kluvs.database.entities.BookEntity

/**
 * Data Access Object for Book entities.
 */
@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBook(bookId: String): BookEntity?

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT lastFetchedAt FROM books WHERE id = :bookId")
    suspend fun getLastFetchedAt(bookId: String): Long?

    @Query("DELETE FROM books")
    suspend fun deleteAll()
}
