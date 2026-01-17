package com.ivangarzab.kluvs.presentation.viewmodels.member

import com.ivangarzab.kluvs.presentation.models.CurrentlyReadingBook
import com.ivangarzab.kluvs.presentation.models.UserProfile
import com.ivangarzab.kluvs.presentation.models.UserStatistics

data class MeState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val profile: UserProfile? = null,
    val statistics: UserStatistics? = null,
    val currentlyReading: List<CurrentlyReadingBook> = emptyList()
)
