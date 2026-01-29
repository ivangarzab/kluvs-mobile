package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.bark.Bark
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
        Bark.d("Fetching club (ID: $clubId, Server: $serverId)")
        return try {
            val response = supabase.functions.invoke("club") {
                method = HttpMethod.Get
                url {
                    parameters.append("id", clubId)
                    // Only append server_id if provided (Discord use case)
                    serverId?.let { parameters.append("server_id", it) }
                }
            }.body<ClubResponseDto>()
            Bark.v("Club fetched successfully (ID: $clubId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to fetch club (ID: $clubId). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun getByChannel(channel: String, serverId: String): ClubResponseDto {
        Bark.d("Fetching club by channel (Channel: $channel, Server: $serverId)")
        return try {
            val response = supabase.functions.invoke("club") {
                method = HttpMethod.Get
                url {
                    parameters.append("discord_channel", channel)
                    parameters.append("server_id", serverId)
                }
            }.body<ClubResponseDto>()
            Bark.v("Club fetched by channel successfully (Channel: $channel)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to fetch club by channel (Channel: $channel). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun create(request: CreateClubRequestDto): ClubSuccessResponseDto {
        Bark.d("Creating club")
        return try {
            val json = getJsonForSupabaseService()
            val jsonString = json.encodeToString(request)

            val response = supabase.functions.invoke("club") {
                method = HttpMethod.Post
                body = jsonString
            }.body<ClubSuccessResponseDto>()
            Bark.v("Club created successfully")
            response
        } catch (error: Exception) {
            Bark.e("Failed to create club. Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun update(request: UpdateClubRequestDto): ClubSuccessResponseDto {
        Bark.d("Updating club (ID: ${request.id})")
        return try {
            val json = getJsonForSupabaseService()
            val jsonString = json.encodeToString(request)

            val response = supabase.functions.invoke("club") {
                method = HttpMethod.Put
                body = jsonString
            }.body<ClubSuccessResponseDto>()
            Bark.v("Club updated successfully (ID: ${request.id})")
            response
        } catch (error: Exception) {
            Bark.e("Failed to update club (ID: ${request.id}). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun delete(clubId: String, serverId: String?): DeleteResponseDto {
        Bark.d("Deleting club (ID: $clubId, Server: $serverId)")
        return try {
            val response = supabase.functions.invoke("club") {
                method = HttpMethod.Delete
                url {
                    parameters.append("id", clubId)
                    // Only append server_id if provided (Discord use case)
                    serverId?.let { parameters.append("server_id", it) }
                }
            }.body<DeleteResponseDto>()
            Bark.v("Club deleted successfully (ID: $clubId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to delete club (ID: $clubId). Check network/API status and retry.", error)
            throw error
        }
    }
}