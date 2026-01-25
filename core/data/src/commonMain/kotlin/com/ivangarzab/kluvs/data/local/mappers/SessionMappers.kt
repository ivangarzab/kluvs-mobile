package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.data.remote.mappers.parseDateString
import com.ivangarzab.kluvs.database.entities.SessionEntity
import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.model.Session
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Maps a [SessionEntity] from the local database to a [Session] domain model.
 * Requires the associated [Book] to be loaded separately.
 * Note: discussions relationship is not loaded from the entity (always empty list).
 */
fun SessionEntity.toDomain(book: Book): Session {
    return Session(
        id = id,
        clubId = requireNotNull(clubId) { "Session must have a clubId" },
        book = book,
        dueDate = dueDate?.parseDateString(),
        discussions = emptyList() // Discussions stored separately, loaded on demand
    )
}

/**
 * Maps a [Session] domain model to a [SessionEntity] for local database storage.
 * Sets lastFetchedAt to current time.
 */
@OptIn(ExperimentalTime::class)
fun Session.toEntity(): SessionEntity {
    return SessionEntity(
        id = id,
        clubId = clubId,
        bookId = book.id,
        dueDate = dueDate?.toString(),
        lastFetchedAt = Clock.System.now().toEpochMilliseconds()
    )
}
