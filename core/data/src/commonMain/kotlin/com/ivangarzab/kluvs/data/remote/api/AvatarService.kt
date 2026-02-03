package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.bark.Bark
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface AvatarService {
    fun getAvatarUrl(avatarPath: String?): String?
    suspend fun uploadAvatar(memberId: String, imageData: ByteArray): Result<String>
    suspend fun deleteAvatar(avatarPath: String): Result<Unit>
}

internal class AvatarServiceImpl(private val supabase: SupabaseClient) : AvatarService {

    override fun getAvatarUrl(avatarPath: String?): String? {
        if (avatarPath.isNullOrBlank()) {
            Bark.d("Avatar path is null or blank, returning null")
            return null
        }
        Bark.d("Generating avatar URL for path: $avatarPath")
        return try {
            val url = supabase.storage.from(BUCKET).publicUrl(avatarPath)
            Bark.v("Avatar URL generated successfully for path: $avatarPath")
            url
        } catch (error: Exception) {
            Bark.e("Failed to generate avatar URL for path: $avatarPath. Check storage status and retry.", error)
            throw error
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun uploadAvatar(
        memberId: String,
        imageData: ByteArray
    ): Result<String> {
        Bark.d("Uploading avatar for member (ID: $memberId, size: ${imageData.size} bytes)")
        return try {
            val path = "$memberId/${Clock.System.now().toEpochMilliseconds()}.png"
            supabase.storage.from(BUCKET).upload(
                path = path,
                data = imageData
            )
            Bark.v("Avatar uploaded successfully (ID: $memberId, path: $path, size: ${imageData.size} bytes)")
            Result.success(path)
        } catch (e: Exception) {
            Bark.e("Failed to upload avatar for member (ID: $memberId). Check network/storage status and retry.", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAvatar(avatarPath: String): Result<Unit> {
        Bark.d("Deleting avatar (path: $avatarPath)")
        return try {
            supabase.storage.from(BUCKET).delete(avatarPath)
            Bark.v("Avatar deleted successfully (path: $avatarPath)")
            Result.success(Unit)
        } catch (e: Exception) {
            Bark.e("Failed to delete avatar (path: $avatarPath). Orphaned file in storage.", e)
            Result.failure(e)
        }
    }

    companion object {
        internal const val BUCKET = "member-avatars"
    }
}