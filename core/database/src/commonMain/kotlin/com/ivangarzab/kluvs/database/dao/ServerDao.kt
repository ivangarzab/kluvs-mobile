package com.ivangarzab.kluvs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivangarzab.kluvs.database.entities.ServerEntity

/**
 * Data Access Object for Server entities.
 */
@Dao
interface ServerDao {
    @Query("SELECT * FROM servers WHERE id = :serverId")
    suspend fun getServer(serverId: String): ServerEntity?

    @Query("SELECT * FROM servers")
    suspend fun getAllServers(): List<ServerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerEntity)

    @Delete
    suspend fun deleteServer(server: ServerEntity)

    @Query("SELECT lastFetchedAt FROM servers WHERE id = :serverId")
    suspend fun getLastFetchedAt(serverId: String): Long?

    @Query("DELETE FROM servers")
    suspend fun deleteAll()
}
