package com.ivangarzab.kluvs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivangarzab.kluvs.database.entities.SessionEntity

/**
 * Data Access Object for Session entities.
 */
@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSession(sessionId: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE clubId = :clubId")
    suspend fun getSessionsForClub(clubId: String): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SessionEntity>)

    @Delete
    suspend fun deleteSession(session: SessionEntity)

    @Query("SELECT lastFetchedAt FROM sessions WHERE id = :sessionId")
    suspend fun getLastFetchedAt(sessionId: String): Long?

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}
