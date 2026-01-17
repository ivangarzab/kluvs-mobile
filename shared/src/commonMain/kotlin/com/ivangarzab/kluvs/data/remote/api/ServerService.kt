package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.kluvs.data.remote.api.JsonHelper.getJsonForSupabaseService
import com.ivangarzab.kluvs.data.remote.dtos.CreateServerRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.DeleteResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerSuccessResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ServersResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateServerRequestDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.utils.io.InternalAPI

interface ServerService {
    suspend fun getAll(): ServersResponseDto
    suspend fun get(serverId: String): ServerResponseDto
    suspend fun create(request: CreateServerRequestDto): ServerSuccessResponseDto
    suspend fun update(request: UpdateServerRequestDto): ServerSuccessResponseDto
    suspend fun delete(serverId: String): DeleteResponseDto
}

@OptIn(InternalAPI::class)
internal class ServerServiceImpl(private val supabase: SupabaseClient) : ServerService {

    override suspend fun getAll(): ServersResponseDto {
        return supabase.functions.invoke("server") {
            method = HttpMethod.Get
        }.body()
    }

    override suspend fun get(serverId: String): ServerResponseDto {
        return supabase.functions.invoke("server") {
            method = HttpMethod.Get
            url { parameters.append("id", serverId) }
        }.body()
    }

    override suspend fun create(request: CreateServerRequestDto): ServerSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("server") {
            method = HttpMethod.Post
            body = jsonString
        }.body()
    }

    override suspend fun update(request: UpdateServerRequestDto): ServerSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("server") {
            method = HttpMethod.Put
            body = jsonString
        }.body()
    }

    override suspend fun delete(serverId: String): DeleteResponseDto {
        return supabase.functions.invoke("server") {
            method = HttpMethod.Delete
            url { parameters.append("id", serverId) }
        }.body()
    }
}