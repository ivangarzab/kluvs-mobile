package com.ivangarzab.kluvs.member.domain

import com.ivangarzab.kluvs.data.repositories.AvatarRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepository

/**
 * Use case for updating a member's avatar.
 *
 * Flow:
 * 1. Upload image to Supabase Storage
 * 2. Update member record with new avatar_path
 * 3. Return the public URL of the new avatar
 */
class UpdateAvatarUseCase(
    private val avatarRepository: AvatarRepository,
    private val memberRepository: MemberRepository
) {
    /**
     * Uploads avatar image and updates member record.
     *
     * @param memberId The member's ID
     * @param imageData Compressed image bytes (PNG format, max 512x512 recommended)
     * @return Result with the new avatar URL on success
     */
    suspend operator fun invoke(memberId: String, imageData: ByteArray): Result<String> {
        // 1. Upload to storage
        val uploadResult = avatarRepository.uploadAvatar(memberId, imageData)
        if (uploadResult.isFailure) {
            return Result.failure(
                uploadResult.exceptionOrNull() ?: Exception("Avatar upload failed")
            )
        }

        val avatarPath = uploadResult.getOrThrow()

        // 2. Update member record with new path
        val updateResult = memberRepository.updateMember(
            memberId = memberId,
            avatarPath = avatarPath
        )

        if (updateResult.isFailure) {
            return Result.failure(
                updateResult.exceptionOrNull() ?: Exception("Failed to update member")
            )
        }

        // 3. Return the public URL
        val avatarUrl = avatarRepository.getAvatarUrl(avatarPath)
        return Result.success(avatarUrl ?: "")
    }
}
