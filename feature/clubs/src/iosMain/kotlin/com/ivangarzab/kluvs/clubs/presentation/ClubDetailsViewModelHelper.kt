package com.ivangarzab.kluvs.clubs.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The purpose of this class is to wrap the [ClubDetailsViewModel] for easier
 * use and access on the iOS side.
 */
@Suppress("unused")
class ClubDetailsViewModelHelper : KoinComponent {

    private val viewModel: ClubDetailsViewModel by inject()
    private val coroutineScope: CoroutineScope by inject()

    /**
     * iOS-friendly observation method.
     *
     * Returns a [com.ivangarzab.kluvs.presentation.viewmodels.Closeable] that can be used to cancel the observation.
     */
    fun observeState(callback: (ClubDetailsState) -> Unit): Closeable {
        val job = viewModel.state.onEach { callback(it) }.launchIn(coroutineScope)
        return Closeable { job.cancel() }
    }

    fun loadUserClubs(userId: String) = viewModel.loadUserClubs(userId)

    fun loadClubData(clubId: String) = viewModel.loadClubData(clubId)

    fun refresh() = viewModel.refresh()
}