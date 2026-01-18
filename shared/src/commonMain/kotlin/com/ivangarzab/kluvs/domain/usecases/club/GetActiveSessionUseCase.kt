package com.ivangarzab.kluvs.domain.usecases.club

import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.domain.usecases.util.FormatDateTimeUseCase
import com.ivangarzab.kluvs.presentation.models.ActiveSessionDetails
import com.ivangarzab.kluvs.presentation.models.BookInfo
import com.ivangarzab.kluvs.presentation.models.DateTimeFormat
import com.ivangarzab.kluvs.presentation.models.DiscussionTimelineItemInfo
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

/**
 * UseCase for fetching active reading session with discussion timeline for ActiveSessionTab.
 *
 * Transforms domain [com.ivangarzab.kluvs.model.Club] model into UI-friendly [ActiveSessionDetails] with:
 * - Book information
 * - Sorted discussions with status indicators (isPast, isNext, isFuture)
 * - Formatted dates
 *
 * @param clubRepository Repository for club data
 * @param formatDateTime UseCase for formatting dates
 */
@OptIn(ExperimentalTime::class)
class GetActiveSessionUseCase(
    private val clubRepository: ClubRepository,
    private val formatDateTime: FormatDateTimeUseCase
) {
    /**
     * Fetches active session details for the specified club.
     *
     * Returns null if the club has no active session.
     * Discussions are sorted chronologically and marked with status flags.
     *
     * @param clubId The ID of the club to retrieve the active session for
     * @return Result containing [ActiveSessionDetails] if successful, null if no active session, or error if failed
     */
    suspend operator fun invoke(clubId: String): Result<ActiveSessionDetails?> {
        return clubRepository.getClub(clubId).map { club: Club ->
            club.activeSession?.let { session ->
                val now = now().toLocalDateTime(TimeZone.currentSystemDefault())
                val sortedDiscussions = session.discussions.sortedBy { it.date }

                // Find the index of the next discussion (first future discussion)
                val nextDiscussionIndex = sortedDiscussions.indexOfFirst { it.date > now }

                ActiveSessionDetails(
                    sessionId = session.id,
                    book = BookInfo(
                        title = session.book.title,
                        author = session.book.author,
                        year = session.book.year?.toString(),
                        pageCount = session.book.pageCount
                    ),
                    dueDate = session.dueDate?.let { formatDateTime(it, DateTimeFormat.DATE_ONLY) } ?: "No due date",
                    discussions = sortedDiscussions.mapIndexed { index, discussion ->
                        DiscussionTimelineItemInfo(
                            id = discussion.id,
                            title = discussion.title,
                            location = discussion.location ?: "TBD...",
                            date = formatDateTime(discussion.date, DateTimeFormat.FULL),
                            // If no upcoming discussions (nextDiscussionIndex == -1), all are past
                            // Otherwise, discussions before the next one are past
                            isPast = nextDiscussionIndex == -1 || index < nextDiscussionIndex,
                            // Only mark as next if there IS a next discussion
                            isNext = nextDiscussionIndex != -1 && index == nextDiscussionIndex
                        )
                    }
                )
            }
        }
    }
}
