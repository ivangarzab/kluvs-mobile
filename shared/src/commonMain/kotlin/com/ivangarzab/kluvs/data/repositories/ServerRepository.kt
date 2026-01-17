package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.remote.dtos.CreateServerRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateServerRequestDto
import com.ivangarzab.kluvs.data.remote.source.ServerRemoteDataSource
import com.ivangarzab.kluvs.domain.models.Server

/**
 * Repository for managing Server data.
 *
 * This repository abstracts the data source layer and provides a clean API for accessing
 * server-related data. Currently delegates to remote data source, but can be extended
 * to support local caching and offline capabilities.
 */
interface ServerRepository {

    /**
     * Retrieves a single server by its ID.
     *
     * @param serverId The ID of the server to retrieve
     * @return Result containing the Server if successful, or an error if the operation failed
     */
    suspend fun getServer(serverId: String): Result<Server>

    /**
     * Retrieves all servers.
     *
     * @return Result containing a list of all Servers if successful, or an error if the operation failed
     */
    suspend fun getAllServers(): Result<List<Server>>

    /**
     * Creates a new server.
     *
     * @param name The name of the server to create
     * @return Result containing the created Server if successful, or an error if the operation failed
     */
    suspend fun createServer(name: String): Result<Server>

    /**
     * Updates an existing server.
     *
     * @param serverId The ID of the server to update
     * @param name Optional new name for the server (null to keep current value)
     * @return Result containing the updated Server if successful, or an error if the operation failed
     */
    suspend fun updateServer(
        serverId: String,
        name: String?
    ): Result<Server>

    /**
     * Deletes a server by its ID.
     *
     * @param serverId The ID of the server to delete
     * @return Result containing success message if deletion was successful, or an error if the operation failed
     */
    suspend fun deleteServer(serverId: String): Result<String>
}

/**
 * Implementation of [ServerRepository] that delegates to remote data sources.
 *
 * This is a simple pass-through implementation that can be extended later to include
 * caching strategies, offline support, and data synchronization.
 */
internal class ServerRepositoryImpl(
    private val serverRemoteDataSource: ServerRemoteDataSource
) : ServerRepository {

    override suspend fun getServer(serverId: String): Result<Server> =
        serverRemoteDataSource.getServer(serverId)

    override suspend fun getAllServers(): Result<List<Server>> =
        serverRemoteDataSource.getAllServers()

    override suspend fun createServer(name: String): Result<Server> =
        serverRemoteDataSource.createServer(
            CreateServerRequestDto(
                name = name,
            )
        )

    override suspend fun updateServer(
        serverId: String,
        name: String?,
    ): Result<Server> =
        serverRemoteDataSource.updateServer(
            UpdateServerRequestDto(
                id = serverId,
                name = name
            )
        )

    override suspend fun deleteServer(serverId: String): Result<String> =
        serverRemoteDataSource.deleteServer(serverId)
}
