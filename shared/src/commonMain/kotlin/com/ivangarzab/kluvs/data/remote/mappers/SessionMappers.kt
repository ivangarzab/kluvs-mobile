package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.SessionDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionResponseDto
import com.ivangarzab.kluvs.model.Session

/**
 * Maps a [SessionDto] from the API to a [com.ivangarzab.kluvs.model.Session] domain model.
 *
 * Note: SessionDto may have partial data (used in nested contexts).
 * For full session data, use [SessionResponseDto.toDomain].
 */
fun SessionDto.toDomain(): Session {
    return Session(
        id = id,
        clubId = club_id ?: "",
        book = book?.toDomain() ?: error("SessionDto missing required book data"),
        dueDate = due_date?.parseDateString(),
        discussions = discussions.map { it.toDomain() }
    )
}

/**
 * Maps a [SessionResponseDto] from the API to a [com.ivangarzab.kluvs.model.Session] domain model.
 *
 * This is the full session response with all related data populated.
 */
fun SessionResponseDto.toDomain(): Session {
    return Session(
        id = id,
        clubId = club.id,
        book = book.toDomain(),
        dueDate = due_date?.parseDateString(),
        discussions = discussions.map { it.toDomain() }
    )
}
