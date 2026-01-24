package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.kluvs.clubs.presentation.MemberListItemInfo
import com.ivangarzab.kluvs.data.repositories.AvatarRepository
import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.model.Member

/**
 * UseCase for fetching club members sorted by points for MembersTab.
 *
 * Transforms domain [Member] models into UI-friendly [MemberListItemInfo] with:
 * - Member information
 * - Points for ranking
 * - Sorted by points (descending)
 *
 * @param clubRepository Repository for club data
 * @param avatarRepository Repository for avatar operations
 */
class GetClubMembersUseCase(
    private val clubRepository: ClubRepository,
    private val avatarRepository: AvatarRepository
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
                    avatarUrl = avatarRepository.getAvatarUrl(member.avatarPath)
                )
            }?.sortedByDescending { it.points } ?: emptyList()
        }
    }
}
