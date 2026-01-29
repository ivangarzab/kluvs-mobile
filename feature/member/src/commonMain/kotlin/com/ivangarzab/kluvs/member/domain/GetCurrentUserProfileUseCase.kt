package com.ivangarzab.kluvs.member.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.AvatarRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.member.presentation.UserProfile
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.kluvs.presentation.util.FormatDateTimeUseCase
import com.ivangarzab.kluvs.presentation.state.DateTimeFormat

/**
 * UseCase for fetching the current user's profile for MeScreen header.
 *
 * Transforms domain Member model into UI-friendly [com.ivangarzab.kluvs.presentation.models.UserProfile] with:
 * - Member information
 * - Generated handle (if not available)
 * - Formatted join date
 *
 * @param memberRepository Repository for member data
 * @param formatDateTime UseCase for formatting dates
 * @param avatarRepository Repository for avatar operations
 */
class GetCurrentUserProfileUseCase(
    private val memberRepository: MemberRepository,
    private val formatDateTime: FormatDateTimeUseCase,
    private val avatarRepository: AvatarRepository
) {
    /**
     * Fetches the profile for the specified user.
     *
     * @param userId The Discord user ID of the current user
     * @return Result containing [com.ivangarzab.kluvs.presentation.models.UserProfile] if successful, or error if failed
     */
    suspend operator fun invoke(userId: String): Result<UserProfile> {
        Bark.d("Fetching current user profile (User ID: $userId)")
        return memberRepository.getMemberByUserId(userId).map { member: Member ->
            val handle = member.handle ?: generateHandleFromName(member.name)
            val profile = UserProfile(
                memberId = member.id,
                name = member.name,
                handle = handle,
                joinDate = member.createdAt?.let {
                    formatDateTime(it, DateTimeFormat.YEAR_ONLY)
                } ?: "2025",
                avatarUrl = avatarRepository.getAvatarUrl(member.avatarPath)
            )
            Bark.i("Loaded user profile (Name: ${member.name}, Handle: $handle)")
            profile
        }.onFailure { error ->
            Bark.e("Failed to fetch user profile (User ID: $userId). User will see error state.", error)
        }
    }

    /**
    * Generates a handle from a member's name.
    * Converts "John Doe" to "@johndoe".
    */
    private fun generateHandleFromName(name: String): String {
        return "@${name.lowercase().replace(" ", "")}"
    }

}
