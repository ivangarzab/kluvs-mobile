package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.remote.api.AvatarService

/**
 * Remote data source for Avatar operations.
 *
 * Responsibilities:
 * - Calls [AvatarService] for storage operations
 * - Wraps results in [Result] for error handling
 */
interface AvatarRemoteDataSource {
    /**
     * Constructs the public URL for an avatar.
     * This is a synchronous operation (no network call).
     */
    fun getAvatarUrl(avatarPath: String?): String?

    /**
     * Uploads avatar image to storage.
     *
     * @param memberId The member's ID (used as folder name)
     * @param imageData Compressed image bytes
     * @return Result with the storage path on success
     */
    suspend fun uploadAvatar(memberId: String, imageData: ByteArray): Result<String>

    /**
     * Deletes an avatar from storage.
     *
     * @param avatarPath The storage path to delete (e.g., "123/1234567890.png")
     * @return Result indicating success or failure
     */
    suspend fun deleteAvatar(avatarPath: String): Result<Unit>
}

internal class AvatarRemoteDataSourceImpl(
    private val avatarService: AvatarService
) : AvatarRemoteDataSource {

    override fun getAvatarUrl(avatarPath: String?): String? {
        return avatarService.getAvatarUrl(avatarPath)
    }

    override suspend fun uploadAvatar(memberId: String, imageData: ByteArray): Result<String> {
        return try {
            avatarService.uploadAvatar(memberId, imageData)
        } catch (e: Exception) {
            Bark.e("Avatar upload failed (Member ID: $memberId). User may need to retry.", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAvatar(avatarPath: String): Result<Unit> {
        return try {
            avatarService.deleteAvatar(avatarPath)
        } catch (e: Exception) {
            Bark.e("Avatar delete failed (path: $avatarPath). Orphaned file in storage.", e)
            Result.failure(e)
        }
    }
}
