package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for discussions.
 * Represents a cached discussion with TTL tracking.
 * Maps to DiscussionDto from the API.
 */
@Entity(tableName = "discussions")
data class DiscussionEntity(
    @PrimaryKey val id: String,
    val sessionId: String?, // References SessionEntity
    val title: String,
    val date: String, // ISO-8601 datetime string
    val location: String?,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
