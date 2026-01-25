package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Discord servers.
 * Represents a cached server with TTL tracking.
 */
@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconUrl: String?,
    val memberCount: Int,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
