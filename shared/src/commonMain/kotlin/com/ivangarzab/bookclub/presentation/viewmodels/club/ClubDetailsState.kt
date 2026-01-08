package com.ivangarzab.bookclub.presentation.viewmodels.club

import com.ivangarzab.bookclub.presentation.models.ActiveSessionDetails
import com.ivangarzab.bookclub.presentation.models.ClubDetails
import com.ivangarzab.bookclub.presentation.models.ClubListItem
import com.ivangarzab.bookclub.presentation.models.MemberListItemInfo

data class ClubDetailsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val availableClubs: List<ClubListItem> = emptyList(),
    val currentClubDetails: ClubDetails? = null,
    val activeSession: ActiveSessionDetails? = null,
    val members: List<MemberListItemInfo> = emptyList()
)