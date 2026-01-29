package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.network.utils.JsonHelper.getJsonForSupabaseService
import com.ivangarzab.kluvs.data.remote.dtos.CreateMemberRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.DeleteResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.MemberResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.MemberSuccessResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateMemberRequestDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.ktor.utils.io.InternalAPI

interface MemberService {
    suspend fun get(memberId: String): MemberResponseDto
    suspend fun getByUserId(userId: String): MemberResponseDto
    suspend fun create(request: CreateMemberRequestDto): MemberSuccessResponseDto
    suspend fun update(request: UpdateMemberRequestDto): MemberSuccessResponseDto
    suspend fun delete(memberId: String): DeleteResponseDto
}

@OptIn(InternalAPI::class)
internal class MemberServiceImpl(private val supabase: SupabaseClient) : MemberService {

    override suspend fun get(memberId: String): MemberResponseDto {
        Bark.d("Fetching member (ID: $memberId)")
        return try {
            val response = supabase.functions.invoke("member") {
                method = HttpMethod.Get
                url { parameters.append("id", memberId) }
            }.body<MemberResponseDto>()
            Bark.v("Member fetched successfully (ID: $memberId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to fetch member (ID: $memberId). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun getByUserId(userId: String): MemberResponseDto {
        Bark.d("Fetching member by user ID (User: $userId)")
        return try {
            val response = supabase.functions.invoke("member") {
                method = HttpMethod.Get
                url { parameters.append("user_id", userId) }
            }.body<MemberResponseDto>()
            Bark.v("Member fetched by user ID successfully (User: $userId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to fetch member by user ID (User: $userId). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun create(request: CreateMemberRequestDto): MemberSuccessResponseDto {
        Bark.d("Creating member")
        return try {
            val json = getJsonForSupabaseService()
            val jsonString = json.encodeToString(request)

            val response = supabase.functions.invoke("member") {
                method = HttpMethod.Post
                body = jsonString
            }.body<MemberSuccessResponseDto>()
            Bark.v("Member created successfully")
            response
        } catch (error: Exception) {
            Bark.e("Failed to create member. Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun update(request: UpdateMemberRequestDto): MemberSuccessResponseDto {
        Bark.d("Updating member (ID: ${request.id})")
        return try {
            val json = getJsonForSupabaseService()
            val jsonString = json.encodeToString(request)

            val response = supabase.functions.invoke("member") {
                method = HttpMethod.Put
                body = jsonString
            }.body<MemberSuccessResponseDto>()
            Bark.v("Member updated successfully (ID: ${request.id})")
            response
        } catch (error: Exception) {
            Bark.e("Failed to update member (ID: ${request.id}). Check network/API status and retry.", error)
            throw error
        }
    }

    override suspend fun delete(memberId: String): DeleteResponseDto {
        Bark.d("Deleting member (ID: $memberId)")
        return try {
            val response = supabase.functions.invoke("member") {
                method = HttpMethod.Delete
                url { parameters.append("id", memberId) }
            }.body<DeleteResponseDto>()
            Bark.v("Member deleted successfully (ID: $memberId)")
            response
        } catch (error: Exception) {
            Bark.e("Failed to delete member (ID: $memberId). Check network/API status and retry.", error)
            throw error
        }
    }
}