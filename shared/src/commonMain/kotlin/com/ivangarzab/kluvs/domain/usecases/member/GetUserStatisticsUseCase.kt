package com.ivangarzab.kluvs.domain.usecases.member

import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.kluvs.presentation.models.UserStatistics

/**
 * UseCase for calculating user statistics for MeScreen StatisticsSection.
 *
 * Aggregates member data across all clubs to provide:
 * - Total clubs count
 * - Total points earned
 * - Total books read
 *
 * @param memberRepository Repository for member data
 */
class GetUserStatisticsUseCase(
    private val memberRepository: MemberRepository
) {
    /**
     * Calculates statistics for the specified user.
     *
     * @param userId The Discord user ID of the current user
     * @return Result containing [UserStatistics] if successful, or error if failed
     */
    suspend operator fun invoke(userId: String): Result<UserStatistics> {
        return memberRepository.getMemberByUserId(userId).map { member: Member ->
            UserStatistics(
                clubsCount = member.clubs?.size ?: 0,
                totalPoints = member.points,
                booksRead = member.booksRead
            )
        }
    }
}
