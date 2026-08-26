package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.MemberRepository

/**
 * UseCase for resolving the signed-in user's own member ID.
 *
 * Deliberately independent of any specific club's member roster (see
 * [GetClubMembersUseCase]) — "what is my own member ID" is a question about the
 * signed-in user, not about a club's roster, and member IDs are global
 * (`memberclubs.member_id` references `members.id` directly — there's no separate
 * per-club membership row). Resolving it this way means a stale or failed roster
 * fetch for one club can no longer silently disable membership-gated actions (the
 * reading opt-in/out toggle, changing a member's role, removing a member) that
 * don't actually depend on that roster at all.
 *
 * @param memberRepository Repository for member data
 */
class GetCurrentMemberIdUseCase(
    private val memberRepository: MemberRepository
) {
    /**
     * Fetches the signed-in user's own member ID.
     *
     * @param userId The auth user ID of the signed-in user
     * @param forceRefresh If true, bypasses cache and fetches fresh data from remote
     * @return Result containing the member ID if successful, or error if failed
     */
    suspend operator fun invoke(userId: String, forceRefresh: Boolean = false): Result<String> {
        Bark.d("Resolving current member ID (User ID: $userId)")
        return memberRepository.getMemberByUserId(userId, forceRefresh).map { it.id }
            .onFailure { error ->
                Bark.e("Failed to resolve current member ID (User ID: $userId).", error)
            }
    }
}
