package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for reading sessions.
 * Represents a cached session with TTL tracking.
 * Maps to SessionDto from the API.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val clubId: String?,
    val bookId: String?, // References BookEntity
    val dueDate: String?, // ISO-8601 datetime string
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
