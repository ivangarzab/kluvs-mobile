package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.ServerEntity
import com.ivangarzab.kluvs.model.Server
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Maps a [ServerEntity] from the local database to a [Server] domain model.
 * Note: clubs relationship is not loaded from the entity (left as null).
 */
fun ServerEntity.toDomain(): Server {
    return Server(
        id = id,
        name = name,
        clubs = null // Relationship not stored in entity
    )
}

/**
 * Maps a [Server] domain model to a [ServerEntity] for local database storage.
 * Sets lastFetchedAt to current time.
 */
@OptIn(ExperimentalTime::class)
fun Server.toEntity(): ServerEntity {
    return ServerEntity(
        id = id,
        name = name,
        lastFetchedAt = Clock.System.now().toEpochMilliseconds()
    )
}
