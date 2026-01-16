package com.ivangarzab.bookclub.presentation.viewmodels.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.bark.Bark
import com.ivangarzab.bookclub.domain.usecases.auth.SignOutUseCase
import com.ivangarzab.bookclub.domain.usecases.member.GetCurrentUserProfileUseCase
import com.ivangarzab.bookclub.domain.usecases.member.GetCurrentlyReadingBooksUseCase
import com.ivangarzab.bookclub.domain.usecases.member.GetUserStatisticsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The purpose of this [ViewModel] class is to serve the Me screen.
 */
class MeViewModel(
    private val getCurrentUserProfile: GetCurrentUserProfileUseCase,
    private val getUserStatistics: GetUserStatisticsUseCase,
    private val getCurrentlyReadingBooks: GetCurrentlyReadingBooksUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MeState())
    val state: StateFlow<MeState> = _state.asStateFlow()

    private var currentUserId: String? = null

    fun loadUserData(userId: String) {
        currentUserId = userId

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Launch all 3 UseCase calls in parallel
            val deferredProfile = async { getCurrentUserProfile(userId) }
            val deferredStats = async { getUserStatistics(userId) }
            val deferredReading = async { getCurrentlyReadingBooks(userId) }

            // Await all results
            val profileResult = deferredProfile.await()
            val statsResult = deferredStats.await()
            val readingResult = deferredReading.await()

            // Aggregate errors
            val errors = listOfNotNull(
                profileResult.exceptionOrNull()?.message,
                statsResult.exceptionOrNull()?.message,
                readingResult.exceptionOrNull()?.message
            )
            val error = when {
                errors.isEmpty() -> null
                errors.distinct().size == 1 -> errors.first() // All errors are identical
                else -> "Multiple errors occurred"
            }
            error?.let { e ->
                Bark.e("Error fetching member details: $e")
            } ?: Bark.v("Got member details successfully")

            // Update state with all results
            _state.update {
                it.copy(
                    isLoading = false,
                    error = error,
                    profile = profileResult.getOrNull(),
                    statistics = statsResult.getOrNull(),
                    currentlyReading = readingResult.getOrNull() ?: emptyList()
                )
            }
        }
    }

    fun refresh() {
        Bark.v("Refreshing member data")
        currentUserId?.let { loadUserData(it) }
    }

    fun signOut() = viewModelScope.launch {
        Bark.d("Signing out")
        signOutUseCase()
    }
}