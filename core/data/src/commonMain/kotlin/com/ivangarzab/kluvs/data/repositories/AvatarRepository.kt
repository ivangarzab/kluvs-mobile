package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.remote.source.AvatarRemoteDataSource

/**
 * Repository for avatar operations.
 */
interface AvatarRepository {

    /**
     * Constructs the public URL for an avatar from its storage path.
     *
     * @param avatarPath The storage path (e.g., "123/avatar.png")
     * @return The full public URL, or null if avatarPath is null/blank
     */
    fun getAvatarUrl(avatarPath: String?): String?

    /**
     * Uploads an avatar image to storage.
     *
     * @param memberId The member's ID
     * @param imageData Compressed image bytes (PNG format recommended)
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

internal class AvatarRepositoryImpl(
    private val avatarRemoteDataSource: AvatarRemoteDataSource
) : AvatarRepository {

    override fun getAvatarUrl(avatarPath: String?): String? {
        return avatarRemoteDataSource.getAvatarUrl(avatarPath)
    }

    override suspend fun uploadAvatar(memberId: String, imageData: ByteArray): Result<String> {
        return avatarRemoteDataSource.uploadAvatar(memberId, imageData)
    }

    override suspend fun deleteAvatar(avatarPath: String): Result<Unit> {
        return avatarRemoteDataSource.deleteAvatar(avatarPath)
    }
}