package com.ivangarzab.kluvs.domain.usecases.member

import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.domain.models.Member
import com.ivangarzab.kluvs.domain.usecases.util.FormatDateTimeUseCase
import com.ivangarzab.kluvs.presentation.models.CurrentlyReadingBook
import com.ivangarzab.kluvs.presentation.models.DateTimeFormat
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

/**
 * UseCase for fetching books the user is currently reading across all clubs.
 *
 * For each club the user belongs to, fetches the active session and returns:
 * - Book title
 * - Club name
 * - Reading progress (calculated from completed discussions)
 * - Due date
 *
 * Progress calculation: Uses discussions as checkpoints. If a session has 4 discussions
 * and 2 have passed, progress is 50% (2/4 = 0.5).
 *
 * @param memberRepository Repository for member data
 * @param clubRepository Repository for club data
 * @param formatDateTime UseCase for formatting dates
 */
@OptIn(ExperimentalTime::class)
class GetCurrentlyReadingBooksUseCase(
    private val memberRepository: MemberRepository,
    private val clubRepository: ClubRepository,
    private val formatDateTime: FormatDateTimeUseCase
) {
    /**
     * Fetches all books the user is currently reading.
     *
     * Returns a list of books from active sessions in all clubs the user is a member of.
     * Progress is calculated based on completed discussions (past discussions / total discussions).
     *
     * Note: Future enhancement could use a reading_progress table for individual progress tracking.
     *
     * @param userId The Discord user ID of the current user
     * @return Result containing list of [CurrentlyReadingBook] if successful, or error if failed
     */
    suspend operator fun invoke(userId: String): Result<List<CurrentlyReadingBook>> {
        return memberRepository.getMemberByUserId(userId).mapCatching { member: Member ->
            val clubs = member.clubs ?: emptyList()
            val currentTime = now().toLocalDateTime(TimeZone.currentSystemDefault())

            // For each club, fetch the active session and create a currently reading entry
            clubs.mapNotNull { club ->
                // Fetch full club details to get active session
                val fullClub = clubRepository.getClub(club.id).getOrNull()
                fullClub?.activeSession?.let { session ->
                    // Calculate progress based on completed discussions
                    val totalDiscussions = session.discussions.size
                    val completedDiscussions = session.discussions.count { it.date < currentTime }
                    val progress = if (totalDiscussions > 0) {
                        completedDiscussions.toFloat() / totalDiscussions.toFloat()
                    } else {
                        0.0f
                    }

                    CurrentlyReadingBook(
                        bookTitle = session.book.title,
                        clubName = club.name,
                        progress = progress,
                        dueDate = session.dueDate?.let { formatDateTime(it, DateTimeFormat.DATE_ONLY) }
                    )
                }
            }
        }
    }
}
