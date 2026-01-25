package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for books.
 * Represents a cached book with TTL tracking.
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String?,
    val year: String?,
    val pageCount: Int?,
    val coverUrl: String?,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
