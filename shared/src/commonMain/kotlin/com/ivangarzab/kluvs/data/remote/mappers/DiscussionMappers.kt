package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.DiscussionDto
import com.ivangarzab.kluvs.domain.models.Discussion

/**
 * Maps a [DiscussionDto] from the API to a [Discussion] domain model.
 */
fun DiscussionDto.toDomain(): Discussion {
    return Discussion(
        id = id,
        sessionId = session_id,
        title = title,
        date = date.parseDateString(),
        location = location
    )
}

/**
 * Maps a [Discussion] domain model to a [DiscussionDto].
 */
fun Discussion.toDto(): DiscussionDto = DiscussionDto(
    id = id,
    session_id = sessionId,
    title = title,
    date = date.toString(),
    location = location
)
