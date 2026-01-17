package com.ivangarzab.kluvs.presentation.viewmodels.club

import com.ivangarzab.kluvs.presentation.models.ActiveSessionDetails
import com.ivangarzab.kluvs.presentation.models.ClubDetails
import com.ivangarzab.kluvs.presentation.models.ClubListItem
import com.ivangarzab.kluvs.presentation.models.MemberListItemInfo

data class ClubDetailsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val availableClubs: List<ClubListItem> = emptyList(),
    val currentClubDetails: ClubDetails? = null,
    val activeSession: ActiveSessionDetails? = null,
    val members: List<MemberListItemInfo> = emptyList()
)