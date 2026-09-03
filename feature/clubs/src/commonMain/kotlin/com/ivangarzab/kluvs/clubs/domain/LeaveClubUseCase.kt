package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.model.Member

/**
 * UseCase for the signed-in member voluntarily leaving a club.
 *
 * Fetches the member's current clubs, removes the target club from the list,
 * and submits the updated memberships via [MemberRepository.updateMember].
 *
 * Restrictions:
 * - The OWNER role is rejected by [BaseAdminUseCase]'s authorization gate — an owner must
 *   transfer ownership (see [TransferOwnershipUseCase]) before they can leave.
 */
class LeaveClubUseCase(
    private val memberRepository: MemberRepository
) : BaseAdminUseCase<LeaveClubUseCase.Params, Member>() {

    override val requiredRoles = NOT_OWNER

    data class Params(
        val memberId: String,
        val clubId: String
    )

    override suspend fun execute(params: Params): Result<Member> {
        Bark.d("Leaving club (Member ID: ${params.memberId}, Club ID: ${params.clubId})")

        // Force a fresh fetch: a TTL-cached club list can be stale (e.g. a club deleted since the
        // last refresh), which would silently compute a bogus "remaining clubs" list and get the
        // whole update rejected by the backend since it validates every club ID still exists.
        val memberResult = memberRepository.getMember(params.memberId, forceRefresh = true)
        if (memberResult.isFailure) {
            Bark.e("Failed to fetch member before leaving club (Member ID: ${params.memberId}).", memberResult.exceptionOrNull())
            return memberResult
        }

        val updatedClubIds = memberResult.getOrThrow().clubs
            ?.filter { it.id != params.clubId }
            ?.map { it.id }
            ?: emptyList()

        return memberRepository.updateMember(
            memberId = params.memberId,
            clubIds = updatedClubIds
        )
            .onSuccess { Bark.i("Left club (Member ID: ${params.memberId}, Club ID: ${params.clubId})") }
            .onFailure { Bark.e("Failed to leave club (Member ID: ${params.memberId}). Retry.", it) }
    }
}
