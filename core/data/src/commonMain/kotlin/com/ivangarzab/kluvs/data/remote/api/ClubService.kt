package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.kluvs.network.utils.JsonHelper.getJsonForSupabaseService
import com.ivangarzab.kluvs.data.remote.dtos.ClubResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ClubSuccessResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.CreateClubRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.DeleteResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateClubRequestDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.utils.io.InternalAPI

interface ClubService {
    suspend fun get(clubId: String, serverId: String? = null): ClubResponseDto
    suspend fun getByChannel(channel: String, serverId: String): ClubResponseDto
    suspend fun create(request: CreateClubRequestDto): ClubSuccessResponseDto
    suspend fun update(request: UpdateClubRequestDto): ClubSuccessResponseDto
    suspend fun delete(clubId: String, serverId: String? = null): DeleteResponseDto
}

@OptIn(InternalAPI::class)
internal class ClubServiceImpl(private val supabase: SupabaseClient) : ClubService {

    override suspend fun get(clubId: String, serverId: String?): ClubResponseDto {
        return supabase.functions.invoke("club") {
            method = HttpMethod.Get
            url {
                parameters.append("id", clubId)
                // Only append server_id if provided (Discord use case)
                serverId?.let { parameters.append("server_id", it) }
            }
        }.body()
    }

    override suspend fun getByChannel(channel: String, serverId: String): ClubResponseDto {
        return supabase.functions.invoke("club") {
            method = HttpMethod.Get
            url {
                parameters.append("discord_channel", channel)
                parameters.append("server_id", serverId)
            }
        }.body()
    }

    override suspend fun create(request: CreateClubRequestDto): ClubSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("club") {
            method = HttpMethod.Post
            body = jsonString
        }.body()
    }

    override suspend fun update(request: UpdateClubRequestDto): ClubSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("club") {
            method = HttpMethod.Put
            body = jsonString
        }.body()
    }

    override suspend fun delete(clubId: String, serverId: String?): DeleteResponseDto {
        return supabase.functions.invoke("club") {
            method = HttpMethod.Delete
            url {
                parameters.append("id", clubId)
                // Only append server_id if provided (Discord use case)
                serverId?.let { parameters.append("server_id", it) }
            }
        }.body()
    }
}