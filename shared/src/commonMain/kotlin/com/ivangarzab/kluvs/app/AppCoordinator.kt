package com.ivangarzab.kluvs.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * App-level coordinator that manages navigation state based on app-wide conditions.
 *
 * Handles:
 * - Authentication state (logged in/out)
 * - Future: Onboarding, force updates, etc.
 *
 * Platform code observes [navigationState] to perform actual navigation.
 */
class AppCoordinator(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Initializing)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    init {
        Bark.d("AppCoordinator initialized")
        viewModelScope.launch {
            // Initialize auth (restore session if exists)
            authRepository.initialize()

            // Observe auth state changes
            authRepository.currentUser.collect { user ->
                _navigationState.value = if (user != null) {
                    Bark.d("User authenticated: ${user.email}")
                    NavigationState.Authenticated(userId = user.id)
                } else {
                    Bark.d("User unauthenticated")
                    NavigationState.Unauthenticated
                }
            }
        }
    }
}

/**
 * Represents app-level navigation state.
 * Platform code maps these states to actual navigation actions.
 */
sealed class NavigationState {
    /**
     * App is initializing (checking auth, loading config, etc.)
     * Platform should show splash screen or loading state.
     */
    data object Initializing : NavigationState()

    /**
     * User is not authenticated.
     * Platform should navigate to auth flow (login/signup).
     */
    data object Unauthenticated : NavigationState()

    /**
     * User is authenticated with the given userId.
     * Platform should navigate to main app.
     */
    data class Authenticated(val userId: String) : NavigationState()
}
