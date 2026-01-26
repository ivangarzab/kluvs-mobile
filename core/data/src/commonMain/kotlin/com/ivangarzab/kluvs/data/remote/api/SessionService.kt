package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.network.utils.JsonHelper.getJsonForSupabaseService
import com.ivangarzab.kluvs.data.remote.dtos.CreateSessionRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.DeleteResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionSuccessResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateSessionRequestDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.utils.io.InternalAPI

interface SessionService {
    suspend fun get(sessionId: String): SessionResponseDto
    suspend fun create(request: CreateSessionRequestDto): SessionSuccessResponseDto
    suspend fun update(request: UpdateSessionRequestDto): SessionSuccessResponseDto
    suspend fun delete(sessionId: String): DeleteResponseDto
}

@OptIn(InternalAPI::class)
internal class SessionServiceImpl(private val supabase: SupabaseClient) : SessionService {

    override suspend fun get(sessionId: String): SessionResponseDto {
        Bark.d("Fetching session (ID: $sessionId)")
        return try {
            val response = supabase.functions.invoke("session") {
                method = HttpMethod.Get
                url { parameters.append("id", sessionId) }
            }.body<SessionResponseDto>()
            Bark.v("Session fetched successfully (ID: $sessionId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to fetch session (ID: $sessionId). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun create(request: CreateSessionRequestDto): SessionSuccessResponseDto {
        Bark.d("Creating session")
        return try {
            val json = getJsonForSupabaseService()
            val jsonString = json.encodeToString(request)

            val response = supabase.functions.invoke("session") {
                method = HttpMethod.Post
                body = jsonString
            }.body<SessionSuccessResponseDto>()
            Bark.v("Session created successfully")
            response
        } catch (error: Exception) {
            Bark.e("Failed to create session. Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun update(request: UpdateSessionRequestDto): SessionSuccessResponseDto {
        Bark.d("Updating session (ID: ${request.id})")
        return try {
            val json = getJsonForSupabaseService()
            val jsonString = json.encodeToString(request)

            val response = supabase.functions.invoke("session") {
                method = HttpMethod.Put
                body = jsonString
            }.body<SessionSuccessResponseDto>()
            Bark.v("Session updated successfully (ID: ${request.id})")
            response
        } catch (error: Exception) {
            Bark.e("Failed to update session (ID: ${request.id}). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun delete(sessionId: String): DeleteResponseDto {
        Bark.d("Deleting session (ID: $sessionId)")
        return try {
            val response = supabase.functions.invoke("session") {
                method = HttpMethod.Delete
                url { parameters.append("id", sessionId) }
            }.body<DeleteResponseDto>()
            Bark.v("Session deleted successfully (ID: $sessionId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to delete session (ID: $sessionId). Check network/API status and retry.", error)
            throw error
        }
    }
}