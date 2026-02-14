package com.ivangarzab.kluvs.clubs.presentation

/**
 * Lightweight UI model for club selection/listing.
 *
 * Contains minimal data needed to display and select clubs.
 * Used for multi-club support where user can switch between clubs.
 */
data class ClubListItem(
    val id: String,
    val name: String
)

/**
 * UI model for club overview displayed in GeneralTab.
 *
 * Contains formatted and derived data ready for direct display.
 */
data class ClubDetails(
    val clubId: String,
    val clubName: String,
    val memberCount: Int,
    val foundedYear: String?,
    val currentBook: BookInfo?,
    val nextDiscussion: DiscussionInfo?
)

/**
 * UI model for active reading session displayed in ActiveSessionTab.
 *
 * Contains the current book being read and a timeline of discussions.
 */
data class ActiveSessionDetails(
    val sessionId: String,
    val book: BookInfo,
    val dueDate: String,
    val discussions: List<DiscussionTimelineItemInfo>
)

/**
 * UI model for a discussion in the timeline.
 *
 * Status flags (isPast, isNext, isFuture) enable UI to render different states.
 */
data class DiscussionTimelineItemInfo(
    val id: String,
    val title: String,
    val location: String,
    val date: String,
    val isPast: Boolean,
    val isNext: Boolean
)

/**
 * UI model for book information.
 *
 * Simplified view of Book domain model with only UI-needed fields.
 */
data class BookInfo(
    val title: String,
    val author: String,
    val year: String?,
    val pageCount: Int?
)

/**
 * UI model for upcoming discussion information.
 *
 * Used in GeneralTab to show next scheduled discussion.
 */
data class DiscussionInfo(
    val title: String,
    val location: String,
    val formattedDate: String
)

/**
 * UI model for member displayed in MembersTab list.
 */
data class MemberListItemInfo(
    val memberId: String,
    val name: String,
    val handle: String,
    val avatarUrl: String?
)
