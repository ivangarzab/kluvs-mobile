package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.ServerDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerResponseDto
import com.ivangarzab.kluvs.domain.models.Server

/**
 * Maps a [ServerDto] from the API to a [Server] domain model.
 *
 * Note: ServerDto contains only basic server info without nested clubs.
 * Relations (clubs) will be null.
 */
fun ServerDto.toDomain(): Server {
    return Server(
        id = id,
        name = name,
        clubs = null
    )
}

/**
 * Maps a [ServerResponseDto] from the API to a [Server] domain model.
 *
 * This is the full server response with all nested relations populated:
 * - clubs (list of Club objects in this server)
 */
fun ServerResponseDto.toDomain(): Server {
    return Server(
        id = id,
        name = name,
        // Map nested ClubDto objects to domain models
        clubs = clubs.map { it.toDomain() }
    )
}
