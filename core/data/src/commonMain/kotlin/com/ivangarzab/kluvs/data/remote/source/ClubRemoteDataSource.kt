package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.remote.api.ClubService
import com.ivangarzab.kluvs.data.remote.dtos.CreateClubRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateClubRequestDto
import com.ivangarzab.kluvs.data.remote.mappers.toDomain
import com.ivangarzab.kluvs.model.Club

/**
 * Remote data source for Club operations.
 *
 * Responsibilities:
 * - Calls [com.ivangarzab.kluvs.data.remote.api.ClubService] to fetch/mutate club data from Supabase
 * - Maps DTOs to domain models using mappers
 * - Wraps results in [Result] for error handling
 */
interface ClubRemoteDataSource {

    /**
     * Fetches a club by ID with optional server ID.
     *
     * Returns a [Club] with all nested relations populated:
     * - members (full Member objects)
     * - activeSession (full Session object)
     * - pastSessions (full Session objects)
     * - shameList (member IDs)
     *
     * @param clubId The ID of the club to retrieve
     * @param serverId Optional server ID for Discord integration (null for mobile-only clubs)
     */
    suspend fun getClub(clubId: String, serverId: String? = null): Result<Club>

    /**
     * Fetches a club by Discord channel ID and server ID.
     *
     * Returns a [Club] with all nested relations populated.
     */
    suspend fun getClubByChannel(channel: String, serverId: String): Result<Club>

    /**
     * Creates a new club.
     *
     * Returns the created [Club] (basic info only, no nested relations).
     */
    suspend fun createClub(request: CreateClubRequestDto): Result<Club>

    /**
     * Updates an existing club.
     *
     * Returns the updated [Club] (basic info only, no nested relations).
     */
    suspend fun updateClub(request: UpdateClubRequestDto): Result<Club>

    /**
     * Deletes a club by ID with optional server ID.
     *
     * Returns success message on successful deletion.
     *
     * @param clubId The ID of the club to delete
     * @param serverId Optional server ID for Discord integration (null for mobile-only clubs)
     */
    suspend fun deleteClub(clubId: String, serverId: String? = null): Result<String>
}

class ClubRemoteDataSourceImpl(
    private val clubService: ClubService
) : ClubRemoteDataSource {

    override suspend fun getClub(clubId: String, serverId: String?): Result<Club> {
        return try {
            val dto = clubService.get(clubId, serverId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            val serverIdMsg = serverId?.let { "from serverId=$it" } ?: "(no serverId)"
            Bark.e("Failed to get club with clubId=$clubId $serverIdMsg", e)
            Result.failure(e)
        }
    }

    override suspend fun getClubByChannel(channel: String, serverId: String): Result<Club> {
        return try {
            val dto = clubService.getByChannel(channel, serverId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to get club from serverId=$serverId by channel=$channel", e)
            Result.failure(e)
        }
    }

    override suspend fun createClub(request: CreateClubRequestDto): Result<Club> {
        return try {
            val response = clubService.create(request)
            Result.success(response.club.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to create club", e)
            Result.failure(e)
        }
    }

    override suspend fun updateClub(request: UpdateClubRequestDto): Result<Club> {
        return try {
            val response = clubService.update(request)
            Result.success(response.club.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to update club with id=${request.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteClub(clubId: String, serverId: String?): Result<String> {
        return try {
            val response = clubService.delete(clubId, serverId)
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception("Delete failed: ${response.message}"))
            }
        } catch (e: Exception) {
            val serverIdMsg = serverId?.let { "from serverId=$it" } ?: "(no serverId)"
            Bark.e("Failed to delete club with id=$clubId $serverIdMsg", e)
            Result.failure(e)
        }
    }
}
