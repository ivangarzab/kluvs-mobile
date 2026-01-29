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
            Bark.i("Fetched all servers (count: ${response.servers.size})")
            Result.success(response.servers.map { it.toDomain() })
        } catch (e: Exception) {
            Bark.e("Failed to fetch all servers. Serving cached data if available.", e)
            Result.failure(e)
        }
    }

    override suspend fun getServer(serverId: String): Result<Server> {
        return try {
            val dto = serverService.get(serverId)
            Bark.i("Fetched server (ID: $serverId)")
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to fetch server (ID: $serverId). Serving cached data if available.", e)
            Result.failure(e)
        }
    }

    override suspend fun createServer(request: CreateServerRequestDto): Result<Server> {
        return try {
            val response = serverService.create(request)
            Bark.i("Server created (name: ${request.name})")
            Result.success(response.server.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to create server. Please retry.", e)
            Result.failure(e)
        }
    }

    override suspend fun updateServer(request: UpdateServerRequestDto): Result<Server> {
        return try {
            val response = serverService.update(request)
            Bark.i("Server updated (ID: ${request.id})")
            Result.success(response.server.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to update server (ID: ${request.id}). Please retry.", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteServer(serverId: String): Result<String> {
        return try {
            val response = serverService.delete(serverId)
            if (response.success) {
                Bark.i("Server deleted (ID: $serverId)")
                Result.success(response.message)
            } else {
                Result.failure(Exception("Delete failed: ${response.message}"))
            }
        } catch (e: Exception) {
            Bark.e("Failed to delete server (ID: $serverId). Please retry.", e)
            Result.failure(e)
        }
    }
}
