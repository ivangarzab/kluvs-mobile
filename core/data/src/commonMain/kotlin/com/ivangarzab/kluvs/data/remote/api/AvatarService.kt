package com.ivangarzab.kluvs.data.remote.api

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage

interface AvatarService {
    fun getAvatarUrl(avatarPath: String?): String?
    suspend fun uploadAvatar(memberId: String, imageData: ByteArray): Result<String>
}

internal class AvatarServiceImpl(private val supabase: SupabaseClient) : AvatarService {

    override fun getAvatarUrl(avatarPath: String?): String? {
        if (avatarPath.isNullOrBlank()) return null
        return supabase.storage.from(BUCKET).publicUrl(avatarPath)
    }

    override suspend fun uploadAvatar(
        memberId: String,
        imageData: ByteArray
    ): Result<String> {
        return try {
            val path = "$memberId/avatar.png"
            supabase.storage.from(BUCKET).upload(
                path = path,
                data = imageData,
                options = { upsert = true }
            )
            Result.success(path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        internal const val BUCKET = "member-avatars"
    }
}