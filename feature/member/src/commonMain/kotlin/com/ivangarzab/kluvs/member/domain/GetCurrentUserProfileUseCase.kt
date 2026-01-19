package com.ivangarzab.kluvs.member.domain

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
 */
class GetCurrentUserProfileUseCase(
    private val memberRepository: MemberRepository,
    private val formatDateTime: FormatDateTimeUseCase
) {
    /**
     * Fetches the profile for the specified user.
     *
     * @param userId The Discord user ID of the current user
     * @return Result containing [com.ivangarzab.kluvs.presentation.models.UserProfile] if successful, or error if failed
     */
    suspend operator fun invoke(userId: String): Result<UserProfile> {
        return memberRepository.getMemberByUserId(userId).map { member: Member ->
            UserProfile(
                memberId = member.id,
                name = member.name,
                handle = member.handle ?: generateHandleFromName(member.name),
                joinDate = member.createdAt?.let {
                    formatDateTime(it, DateTimeFormat.YEAR_ONLY)
                } ?: "2025",
                avatarUrl = null // TODO: Add avatar support when available
            )
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
