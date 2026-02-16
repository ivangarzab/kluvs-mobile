package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for books.
 * Represents a cached book with TTL tracking.
 * Maps to BookDto from the API.
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val edition: String?,
    val year: Int?,
    val isbn: String?,
    val pageCount: Int?,
    val imageUrl: String?,
    val externalGoogleId: String?,
    val lastFetchedAt: Long // Timestamp in milliseconds for TTL check
)
