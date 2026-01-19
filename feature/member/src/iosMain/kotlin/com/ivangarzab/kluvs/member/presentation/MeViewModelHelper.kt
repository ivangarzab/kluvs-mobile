package com.ivangarzab.kluvs.member.presentation

import com.ivangarzab.kluvs.presentation.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The purpose of this class is to wrap the [MeViewModelHelper] for easier
 * use and access on the iOS side.
 */
@Suppress("unused")
class MeViewModelHelper : KoinComponent {

    private val viewModel: MeViewModel by inject()
    private val coroutineScope: CoroutineScope by inject()

    /**
     * iOS-friendly observation method.
     *
     * Returns a [com.ivangarzab.kluvs.presentation.Closeable] that can be used to cancel the observation.
     */
    fun observeState(callback: (MeState) -> Unit): Closeable {
        val job = viewModel.state.onEach { callback(it) }.launchIn(coroutineScope)
        return Closeable { job.cancel() }
    }

    fun loadUserData(userId: String) = viewModel.loadUserData(userId)

    fun refresh() = viewModel.refresh()
}