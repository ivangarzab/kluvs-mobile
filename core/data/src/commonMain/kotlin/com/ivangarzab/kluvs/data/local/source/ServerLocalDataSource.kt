package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.model.Server
import com.ivangarzab.bark.Bark

/**
 * Local data source for Server entities.
 * Handles CRUD operations with the local Room database.
 */
interface ServerLocalDataSource {
    suspend fun getServer(serverId: String): Server?
    suspend fun getAllServers(): List<Server>
    suspend fun insertServer(server: Server)
    suspend fun deleteServer(serverId: String)
    suspend fun getLastFetchedAt(serverId: String): Long?
    suspend fun deleteAll()
}

/**
 * Implementation of [ServerLocalDataSource] using Room database.
 */
class ServerLocalDataSourceImpl(
    private val database: KluvsDatabase
) : ServerLocalDataSource {

    private val serverDao = database.serverDao()

    override suspend fun getServer(serverId: String): Server? {
        return serverDao.getServer(serverId)?.toDomain()
    }

    override suspend fun getAllServers(): List<Server> {
        return serverDao.getAllServers().map { it.toDomain() }
    }

    override suspend fun insertServer(server: Server) {
        Bark.d("Inserting server (ID: ${server.id}) into database")
        try {
            serverDao.insertServer(server.toEntity())
            Bark.d("Successfully inserted server (ID: ${server.id}) into database")
        } catch (e: Exception) {
            Bark.e("Failed to insert server (ID: ${server.id}) into database. Retry on next sync.", e)
            throw e
        }
    }

    override suspend fun deleteServer(serverId: String) {
        val entity = serverDao.getServer(serverId)
        if (entity != null) {
            Bark.d("Deleting server (ID: $serverId) from database")
            try {
                serverDao.deleteServer(entity)
                Bark.d("Successfully deleted server (ID: $serverId) from database")
            } catch (e: Exception) {
                Bark.e("Failed to delete server (ID: $serverId) from database. Retry on next sync.", e)
                throw e
            }
        }
    }

    override suspend fun getLastFetchedAt(serverId: String): Long? {
        return serverDao.getLastFetchedAt(serverId)
    }

    override suspend fun deleteAll() {
        Bark.d("Clearing all servers from database")
        try {
            serverDao.deleteAll()
            Bark.d("Successfully cleared all servers from database")
        } catch (e: Exception) {
            Bark.e("Failed to clear all servers from database. Retry on next sync.", e)
            throw e
        }
    }
}
