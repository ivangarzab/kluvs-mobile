package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for book clubs.
 * Represents a cached club with TTL tracking.
 */
@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey val id: String,
    val serverId: String?,
    val name: String,
    val discordChannel: String?,
    val foundedDate: String?,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
