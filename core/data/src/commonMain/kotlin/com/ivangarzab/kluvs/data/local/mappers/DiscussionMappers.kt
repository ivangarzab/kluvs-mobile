package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.data.remote.mappers.parseDateString
import com.ivangarzab.kluvs.database.entities.DiscussionEntity
import com.ivangarzab.kluvs.model.Discussion
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Maps a [DiscussionEntity] from the local database to a [Discussion] domain model.
 */
fun DiscussionEntity.toDomain(): Discussion {
    return Discussion(
        id = id,
        sessionId = sessionId,
        title = title,
        date = date.parseDateString(),
        location = location
    )
}

/**
 * Maps a [Discussion] domain model to a [DiscussionEntity] for local database storage.
 * Sets lastFetchedAt to current time.
 */
@OptIn(ExperimentalTime::class)
fun Discussion.toEntity(): DiscussionEntity {
    return DiscussionEntity(
        id = id,
        sessionId = sessionId,
        title = title,
        date = date.toString(),
        location = location,
        lastFetchedAt = Clock.System.now().toEpochMilliseconds()
    )
}
