package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.remote.api.SessionService
import com.ivangarzab.kluvs.data.remote.dtos.CreateSessionRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateSessionRequestDto
import com.ivangarzab.kluvs.data.remote.mappers.toDomain
import com.ivangarzab.kluvs.model.Session

/**
 * Remote data source for Session operations.
 *
 * Responsibilities:
 * - Calls [com.ivangarzab.kluvs.data.remote.api.SessionService] to fetch/mutate session data from Supabase
 * - Maps DTOs to domain models using mappers
 * - Wraps results in [Result] for error handling
 */
interface SessionRemoteDataSource {

    /**
     * Fetches a session by ID.
     *
     * Returns a [Session] with all nested relations populated:
     * - book (full Book object)
     * - discussions (full Discussion objects)
     * - clubId (extracted from nested Club)
     */
    suspend fun getSession(sessionId: String): Result<Session>

    /**
     * Creates a new session.
     *
     * Returns the created [Session] with nested relations if available.
     */
    suspend fun createSession(request: CreateSessionRequestDto): Result<Session>

    /**
     * Updates an existing session.
     *
     * Returns the updated [Session] with nested relations if available.
     */
    suspend fun updateSession(request: UpdateSessionRequestDto): Result<Session>

    /**
     * Deletes a session by ID.
     *
     * Returns success message on successful deletion.
     */
    suspend fun deleteSession(sessionId: String): Result<String>
}

class SessionRemoteDataSourceImpl(
    private val sessionService: SessionService
) : SessionRemoteDataSource {

    override suspend fun getSession(sessionId: String): Result<Session> {
        return try {
            val dto = sessionService.get(sessionId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to fetch session with id=$sessionId", e)
            Result.failure(e)
        }
    }

    override suspend fun createSession(request: CreateSessionRequestDto): Result<Session> {
        return try {
            val response = sessionService.create(request)
            // Response contains SessionDto which may have partial data
            val session = response.session
                ?: throw Exception("Session creation succeeded but no session returned")
            Result.success(session.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to create session", e)
            Result.failure(e)
        }
    }

    override suspend fun updateSession(request: UpdateSessionRequestDto): Result<Session> {
        return try {
            val response = sessionService.update(request)
            val session = response.session
                ?: throw Exception("Session update succeeded but no session returned")
            Result.success(session.toDomain())
        } catch (e: Exception) {
            Bark.e("Failed to update session with id=${request.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<String> {
        return try {
            val response = sessionService.delete(sessionId)
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception("Delete failed: ${response.message}"))
            }
        } catch (e: Exception) {
            Bark.e("Failed to delete session with id=$sessionId", e)
            Result.failure(e)
        }
    }
}
