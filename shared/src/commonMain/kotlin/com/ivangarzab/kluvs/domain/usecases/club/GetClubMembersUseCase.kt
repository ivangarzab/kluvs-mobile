package com.ivangarzab.kluvs.domain.usecases.club

import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.kluvs.presentation.models.MemberListItemInfo

/**
 * UseCase for fetching club members sorted by points for MembersTab.
 *
 * Transforms domain [com.ivangarzab.kluvs.model.Member] models into UI-friendly [MemberListItemInfo] with:
 * - Member information
 * - Points for ranking
 * - Sorted by points (descending)
 *
 * @param clubRepository Repository for club data
 */
class GetClubMembersUseCase(
    private val clubRepository: ClubRepository
) {
    /**
     * Fetches club members sorted by points.
     *
     * Members are returned in descending order by points (highest first).
     * Returns empty list if club has no members.
     *
     * @param clubId The ID of the club to retrieve members for
     * @return Result containing list of [MemberListItemInfo] if successful, or error if failed
     */
    suspend operator fun invoke(clubId: String): Result<List<MemberListItemInfo>> {
        return clubRepository.getClub(clubId).map { club: Club ->
            club.members?.map { member: Member ->
                MemberListItemInfo(
                    memberId = member.id,
                    name = member.name,
                    handle = member.handle ?: "@",
                    points = member.points,
                    avatarUrl = null // TODO: Add avatar support when available
                )
            }?.sortedByDescending { it.points } ?: emptyList()
        }
    }
}
