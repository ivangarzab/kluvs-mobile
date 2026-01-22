package com.ivangarzab.kluvs.member.presentation

data class MeState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val profile: UserProfile? = null,
    val statistics: UserStatistics? = null,
    val currentlyReading: List<CurrentlyReadingBook> = emptyList(),
    val showLogoutConfirmation: Boolean = false
)