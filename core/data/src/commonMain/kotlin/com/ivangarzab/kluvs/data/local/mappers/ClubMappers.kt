package com.ivangarzab.kluvs.data.local.mappers

import com.ivangarzab.kluvs.database.entities.ClubEntity
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.network.utils.parseDateOnlyString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Maps a [ClubEntity] from the local database to a [Club] domain model.
 * Note: members, activeSession, and pastSessions relationships are not loaded from the entity (left as null).
 */
fun ClubEntity.toDomain(): Club {
    return Club(
        id = id,
        name = name,
        discordChannel = discordChannel,
        serverId = serverId,
        foundedDate = parseDateOnlyString(foundedDate),
        shameList = emptyList(), // Stored separately in database
        members = null, // Relationship not stored in entity
        activeSession = null, // Relationship not stored in entity
        pastSessions = null // Relationship not stored in entity
    )
}

/**
 * Maps a [Club] domain model to a [ClubEntity] for local database storage.
 * Sets lastFetchedAt to current time.
 */
@OptIn(ExperimentalTime::class)
fun Club.toEntity(): ClubEntity {
    return ClubEntity(
        id = id,
        serverId = serverId,
        name = name,
        discordChannel = discordChannel,
        foundedDate = foundedDate?.toString(),
        lastFetchedAt = Clock.System.now().toEpochMilliseconds()
    )
}
