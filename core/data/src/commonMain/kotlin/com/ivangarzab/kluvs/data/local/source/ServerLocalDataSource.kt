package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.model.Server

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
        serverDao.insertServer(server.toEntity())
    }

    override suspend fun deleteServer(serverId: String) {
        val entity = serverDao.getServer(serverId)
        if (entity != null) {
            serverDao.deleteServer(entity)
        }
    }

    override suspend fun getLastFetchedAt(serverId: String): Long? {
        return serverDao.getLastFetchedAt(serverId)
    }

    override suspend fun deleteAll() {
        serverDao.deleteAll()
    }
}
