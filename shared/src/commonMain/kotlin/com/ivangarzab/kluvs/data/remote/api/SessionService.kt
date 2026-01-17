package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.kluvs.data.remote.api.JsonHelper.getJsonForSupabaseService
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
        return supabase.functions.invoke("session") {
            method = HttpMethod.Get
            url { parameters.append("id", sessionId) }
        }.body()
    }

    override suspend fun create(request: CreateSessionRequestDto): SessionSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("session") {
            method = HttpMethod.Post
            body = jsonString
        }.body()
    }

    override suspend fun update(request: UpdateSessionRequestDto): SessionSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("session") {
            method = HttpMethod.Put
            body = jsonString
        }.body()
    }

    override suspend fun delete(sessionId: String): DeleteResponseDto {
        return supabase.functions.invoke("session") {
            method = HttpMethod.Delete
            url { parameters.append("id", sessionId) }
        }.body()
    }
}