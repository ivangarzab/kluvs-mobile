package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for reading sessions.
 * Represents a cached session with TTL tracking.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val clubId: String,
    val bookId: String?,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
