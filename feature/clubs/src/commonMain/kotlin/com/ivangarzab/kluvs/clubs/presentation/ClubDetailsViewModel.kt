package com.ivangarzab.kluvs.clubs.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.clubs.domain.GetActiveSessionUseCase
import com.ivangarzab.kluvs.clubs.domain.GetClubDetailsUseCase
import com.ivangarzab.kluvs.clubs.domain.GetMemberClubsUseCase
import com.ivangarzab.kluvs.clubs.domain.GetClubMembersUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The purpose of this [ViewModel] class is to serve the Club screen.
 */
class ClubDetailsViewModel(
    private val getClubDetails: GetClubDetailsUseCase,
    private val getActiveSession: GetActiveSessionUseCase,
    private val getClubMembers: GetClubMembersUseCase,
    private val getMemberClubsUseCase: GetMemberClubsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClubDetailsState())
    val state: StateFlow<ClubDetailsState> = _state.asStateFlow()

    private var currentClubId: String? = null

    /**
     * Loads the user's clubs and displays the first club.
     * If the user has no clubs, sets state to show empty state.
     */
    fun loadUserClubs(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getMemberClubsUseCase(userId)
                .onSuccess { clubListItems ->
                    _state.update { it.copy(availableClubs = clubListItems) }

                    if (clubListItems.isNotEmpty()) {
                        Bark.i("Loaded ${clubListItems.size} club(s) for user (ID: $userId)")
                        // Load full details for the first club only
                        val firstClubId = clubListItems.first().id
                        _state.update { it.copy(selectedClubId = firstClubId) }
                        loadClubData(firstClubId)
                    } else {
                        Bark.i("User has no clubs (ID: $userId)")
                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
                .onFailure { error ->
                    Bark.e("Failed to load member clubs for user (ID: $userId). Please retry.", error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load clubs"
                        )
                    }
                }
        }
    }

    fun selectClub(clubId: String) {
        _state.update { it.copy(selectedClubId = clubId) }
        loadClubData(clubId)
    }

    fun loadClubData(clubId: String) {
        currentClubId = clubId

        viewModelScope.launch {
            // Reset state for subsequent calls
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    currentClubDetails = null,
                    activeSession = null,
                    members = emptyList()
                )
            }

            // Launch all 3 UseCase calls in parallel
            val deferredDetails = async { getClubDetails(clubId) }
            val deferredSession = async { getActiveSession(clubId) }
            val deferredMembers = async { getClubMembers(clubId) }

            // Await all results
            val detailsResult = deferredDetails.await()
            val sessionResult = deferredSession.await()
            val membersResult = deferredMembers.await()

            // Aggregate errors
            val errors = listOfNotNull(
                detailsResult.exceptionOrNull()?.message,
                sessionResult.exceptionOrNull()?.message,
                membersResult.exceptionOrNull()?.message
            )
            val error = when {
                errors.isEmpty() -> null
                errors.distinct().size == 1 -> errors.first() // All errors are identical
                else -> "Multiple errors occurred"
            }
            error?.let { e ->
                Bark.e("Failed to fetch club details (ID: $clubId). Serving cached data if available.", Exception(e))
            } ?: Bark.i("Successfully loaded club details (ID: $clubId)")

            // Update state with all results
            _state.update {
                it.copy(
                    isLoading = false,
                    error = error,
                    selectedClubId = clubId,
                    currentClubDetails = detailsResult.getOrNull(),
                    activeSession = sessionResult.getOrNull(),
                    members = membersResult.getOrNull() ?: emptyList()
                )
            }
        }
    }

    fun refresh() {
        Bark.d("Refreshing club data")
        currentClubId?.let { loadClubData(it) }
    }
}