package com.ivangarzab.kluvs.data.remote.api

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
        return supabase.functions.invoke("member") {
            method = HttpMethod.Get
            url { parameters.append("id", memberId) }
        }.body()
    }

    override suspend fun getByUserId(userId: String): MemberResponseDto {
        return supabase.functions.invoke("member") {
            method = HttpMethod.Get
            url { parameters.append("user_id", userId) }
        }.body()
    }

    override suspend fun create(request: CreateMemberRequestDto): MemberSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("member") {
            method = HttpMethod.Post
            body = jsonString
        }.body()
    }

    override suspend fun update(request: UpdateMemberRequestDto): MemberSuccessResponseDto {
        val json = getJsonForSupabaseService()
        val jsonString = json.encodeToString(request)

        return supabase.functions.invoke("member") {
            method = HttpMethod.Put
            body = jsonString
        }.body()
    }

    override suspend fun delete(memberId: String): DeleteResponseDto {
        return supabase.functions.invoke("member") {
            method = HttpMethod.Delete
            url { parameters.append("id", memberId) }
        }.body()
    }
}