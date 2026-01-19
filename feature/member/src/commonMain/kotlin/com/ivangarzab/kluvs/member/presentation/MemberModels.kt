package com.ivangarzab.kluvs.member.presentation

/**
 * UI model for current user's profile displayed in MeScreen header.
 */
data class UserProfile(
    val memberId: String,
    val name: String,
    val handle: String?,
    val joinDate: String,
    val avatarUrl: String?
)

/**
 * UI model for user statistics displayed in MeScreen StatisticsSection.
 *
 * Aggregates user metrics across all clubs.
 */
data class UserStatistics(
    val clubsCount: Int,
    val totalPoints: Int,
    val booksRead: Int
)

/**
 * UI model for a book the user is currently reading.
 *
 * Displayed in MeScreen CurrentlyReadingSection with progress indicator.
 */
data class CurrentlyReadingBook(
    val bookTitle: String,
    val clubName: String,
    val progress: Float, // 0.0 to 1.0
    val dueDate: String?
)
