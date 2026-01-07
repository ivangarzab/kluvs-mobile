package com.ivangarzab.bookclub.presentation.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.bark.Bark
import com.ivangarzab.bookclub.domain.usecases.member.GetMemberClubsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getMemberClubsUseCase: GetMemberClubsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun loadUserClub(userId: String) = viewModelScope.launch {
        getMemberClubsUseCase(userId)
            .onSuccess { clubs ->
                if (clubs.isNotEmpty()) {
                    _state.update {
                        // TODO: Defaulting to first club in list until we implement multi-club support
                        it.copy(clubId = clubs.first().id)
                    }
                } else {
                    //TODO: What if there are no clubs?
                    // Perhaps, we refactor the ClubDetailsView to show an empty state screen
                }
            }
            .onFailure { error ->
                Bark.e("Failed to load member clubs", error)
            }
    }
}

data class MainState(
    val clubId: String? = null,
)