package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.BookEntity
import com.ivangarzab.kluvs.model.Book
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Maps a [BookEntity] from the local database to a [Book] domain model.
 */
fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        edition = edition,
        year = year,
        isbn = isbn,
        pageCount = pageCount
    )
}

/**
 * Maps a [Book] domain model to a [BookEntity] for local database storage.
 * Sets lastFetchedAt to current time.
 */
@OptIn(ExperimentalTime::class)
fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = requireNotNull(id) { "Book ID cannot be null when caching" },
        title = title,
        author = author,
        edition = edition,
        year = year,
        isbn = isbn,
        pageCount = pageCount,
        lastFetchedAt = Clock.System.now().toEpochMilliseconds()
    )
}
