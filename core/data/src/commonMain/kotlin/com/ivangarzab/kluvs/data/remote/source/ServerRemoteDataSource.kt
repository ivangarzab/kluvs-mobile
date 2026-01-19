package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.remote.api.ServerService
import com.ivangarzab.kluvs.data.remote.dtos.CreateServerRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateServerRequestDto
import com.ivangarzab.kluvs.data.remote.mappers.toDomain
import com.ivangarzab.kluvs.model.Server

/**
 * Remote data source for Server operations.
 *
 * Responsibilities:
 * - Calls [com.ivangarzab.kluvs.data.remote.api.ServerService] to fetch/mutate server data from Supabase
 * - Maps DTOs to domain models using mappers
 * - Wraps results in [Result] for error handling
 */
interface ServerRemoteDataSource {

    /**
     * Fetches all servers.
     *
     * Returns a list of [Server] objects with nested clubs populated.
     */
    suspend fun getAllServers(): Result<List<Server>>

    /**
     * Fetches a server by ID.
     *
     * Returns a [Server] with all nested relations populated:
     * - clubs (full Club objects in this server)
     */
    suspend fun getServer(serverId: String): Result<Server>

    /**
     * Creates a new server.
     *
     * Returns the created [Server] (basic info only, no nested clubs).
     */
    suspend fun createServer(request: CreateServerRequestDto): Result<Server>

    /**
     * Updates an existing server.
     *
     * Returns the updated [Server] (basic info only, no nested clubs).
     */
    suspend fun updateServer(request: UpdateServerRequestDto): Result<Server>

    /**
     * Deletes a server by ID.
     *
     * Returns success message on successful deletion.
     */
    suspend fun deleteServer(serverId: String): Result<String>
}

class ServerRemoteDataSourceImpl(
    private val serverService: ServerService
) : ServerRemoteDataSource {

    override suspend fun getAllServers(): Result<List<Server>> {
        return try {
            val response = serverService.getAll()
            Result.success(response.servers.map { it.toDomain() })
        } catch (e: Exception) {
            Bark.e("Failed to fetch all servers list", e)
            Result.failure(e)
        }
    }

    override suspend fun getServer(serverId: String): Result<Server> {
        return try {
            val dto = serverService.get(serverId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to fetch server with id=$serverId", e)
            Result.failure(e)
        }
    }

    override suspend fun createServer(request: CreateServerRequestDto): Result<Server> {
        return try {
            val response = serverService.create(request)
            Result.success(response.server.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to create server", e)
            Result.failure(e)
        }
    }

    override suspend fun updateServer(request: UpdateServerRequestDto): Result<Server> {
        return try {
            val response = serverService.update(request)
            Result.success(response.server.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to update server with id=${request.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteServer(serverId: String): Result<String> {
        return try {
            val response = serverService.delete(serverId)
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception("Delete failed: ${response.message}"))
            }
        } catch (e: Exception) {
            Bark.e("Failed to delete server with id=$serverId", e)
            Result.failure(e)
        }
    }
}
