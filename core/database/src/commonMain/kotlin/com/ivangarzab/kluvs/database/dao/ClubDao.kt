package com.ivangarzab.kluvs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivangarzab.kluvs.database.entities.ClubEntity

/**
 * Data Access Object for Club entities.
 */
@Dao
interface ClubDao {
    @Query("SELECT * FROM clubs WHERE id = :clubId")
    suspend fun getClub(clubId: String): ClubEntity?

    @Query("SELECT * FROM clubs WHERE serverId = :serverId")
    suspend fun getClubsForServer(serverId: String): List<ClubEntity>

    @Query("SELECT * FROM clubs")
    suspend fun getAllClubs(): List<ClubEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: ClubEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<ClubEntity>)

    @Delete
    suspend fun deleteClub(club: ClubEntity)

    @Query("SELECT lastFetchedAt FROM clubs WHERE id = :clubId")
    suspend fun getLastFetchedAt(clubId: String): Long?

    @Query("DELETE FROM clubs")
    suspend fun deleteAll()
}
