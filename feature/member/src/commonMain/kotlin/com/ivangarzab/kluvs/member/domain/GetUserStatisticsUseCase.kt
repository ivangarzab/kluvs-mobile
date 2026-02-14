package com.ivangarzab.kluvs.member.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.member.presentation.UserStatistics
import com.ivangarzab.kluvs.model.Member

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
     * @return Result containing [com.ivangarzab.kluvs.presentation.models.UserStatistics] if successful, or error if failed
     */
    suspend operator fun invoke(userId: String): Result<UserStatistics> {
        Bark.d("Fetching user statistics (User ID: $userId)")
        return memberRepository.getMemberByUserId(userId).map { member: Member ->
            val stats = UserStatistics(
                clubsCount = member.clubs?.size ?: 0,
                booksRead = member.booksRead
            )
            Bark.i("Loaded user statistics (Clubs: ${stats.clubsCount}, Books: ${stats.booksRead})")
            stats
        }.onFailure { error ->
            Bark.e("Failed to fetch user statistics (User ID: $userId). User will see default stats.", error)
        }
    }
}
