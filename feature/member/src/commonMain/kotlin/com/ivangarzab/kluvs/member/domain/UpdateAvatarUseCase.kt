package com.ivangarzab.kluvs.member.domain

import com.ivangarzab.bark.Bark
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
        Bark.d("Updating avatar (Member ID: $memberId, Image size: ${imageData.size} bytes)")

        // 1. Capture the current avatar path so we can delete it after the swap
        val oldAvatarPath = memberRepository
            .getMember(memberId)
            .getOrNull()
            ?.avatarPath

        // 2. Upload to storage
        val uploadResult = avatarRepository.uploadAvatar(memberId, imageData)
        if (uploadResult.isFailure) {
            val error = uploadResult.exceptionOrNull() ?: Exception("Avatar upload failed")
            Bark.e("Failed to upload avatar to storage (Member ID: $memberId). Retrying with fallback.", error)
            return Result.failure(error)
        }

        val avatarPath = uploadResult.getOrThrow()
        Bark.d("Avatar uploaded to storage (Member ID: $memberId, Path: $avatarPath)")

        // 3. Update member record with new path
        val updateResult = memberRepository.updateMember(
            memberId = memberId,
            avatarPath = avatarPath
        )

        if (updateResult.isFailure) {
            val error = updateResult.exceptionOrNull() ?: Exception("Failed to update member")
            Bark.e("Failed to update member record with new avatar (Member ID: $memberId). Avatar uploaded but member not updated.", error)
            return Result.failure(error)
        }

        Bark.d("Member record updated with new avatar path (Member ID: $memberId)")

        // 4. Delete the old avatar now that the member record points to the new one.
        //    Failure here is non-fatal — it just leaves an orphaned file in storage.
        if (oldAvatarPath != null) {
            avatarRepository.deleteAvatar(oldAvatarPath)
                .onFailure { error ->
                    Bark.e("Failed to delete old avatar (path: $oldAvatarPath). Orphaned file in storage.", error)
                }
        }

        // 5. Return the public URL
        val avatarUrl = avatarRepository.getAvatarUrl(avatarPath)
        Bark.i("Avatar updated successfully (Member ID: $memberId)")
        return Result.success(avatarUrl ?: "")
    }
}
