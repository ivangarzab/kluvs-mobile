package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Discord servers.
 * Represents a cached server with TTL tracking.
 * Maps to ServerDto from the API.
 */
@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
