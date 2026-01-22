package com.ivangarzab.kluvs.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Singleton handler for OAuth callbacks.
 *
 * MainActivity posts callback URLs here, and the AuthViewModel collects them.
 */
object OAuthCallbackHandler {
    private val _callbacks = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val callbacks: SharedFlow<String> = _callbacks.asSharedFlow()

    fun handleCallback(url: String) {
        _callbacks.tryEmit(url)
    }
}
