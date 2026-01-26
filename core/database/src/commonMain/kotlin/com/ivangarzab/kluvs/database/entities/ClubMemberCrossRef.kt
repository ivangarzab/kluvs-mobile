package com.ivangarzab.kluvs.database.entities

import androidx.room.Entity

/**
 * Room cross-reference entity for Club-Member many-to-many relationship.
 * Tracks which members belong to which clubs.
 */
@Entity(
    tableName = "club_members",
    primaryKeys = ["clubId", "memberId"]
)
data class ClubMemberCrossRef(
    val clubId: String,
    val memberId: String
)
