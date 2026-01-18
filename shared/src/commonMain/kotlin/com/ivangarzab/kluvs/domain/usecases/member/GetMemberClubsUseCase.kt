package com.ivangarzab.kluvs.domain.usecases.member

import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.presentation.models.ClubListItem
import kotlin.collections.emptyList

/**
 * UseCase for fetching the current user's [com.ivangarzab.kluvs.model.Club] list for the MainScreen.
 *
 * Extract list of clubs form the Member model into a UI-friendly list.
 *
 * @param memberRepository Repository for member data
 */
class GetMemberClubsUseCase(
    private val memberRepository: MemberRepository
) {
    /**
     * Fetches all clubs for a member by their user ID.
     *
     * @param userId The auth user ID to look up
     * @return Result containing list of [ClubListItem], or error if failed
     */
    suspend operator fun invoke(userId: String): Result<List<ClubListItem>> {
        return memberRepository.getMemberByUserId(userId).map { member ->
            member.clubs?.map { club ->
                ClubListItem(
                    id = club.id,
                    name = club.name
                )
            } ?: emptyList()
        }
    }

}