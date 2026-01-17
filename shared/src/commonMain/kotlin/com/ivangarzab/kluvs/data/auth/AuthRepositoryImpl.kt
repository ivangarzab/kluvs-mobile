package com.ivangarzab.kluvs.data.auth

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.auth.mappers.toAuthError
import com.ivangarzab.kluvs.data.auth.mappers.toDomain
import com.ivangarzab.kluvs.data.local.storage.SecureStorage
import com.ivangarzab.kluvs.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of [AuthRepository].
 *
 * Coordinates between:
 * - [AuthService] for Supabase authentication
 * - [SecureStorage] for persistent token storage
 * - StateFlow for reactive auth state
 */
class AuthRepositoryImpl(
    private val authService: AuthService,
    private val secureStorage: SecureStorage
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun initialize(): Result<User?> {
        Bark.v("Initializing AuthRepository")

        return try {
            // Check if we have stored tokens
            val accessToken = secureStorage.get(SecureStorage.KEY_ACCESS_TOKEN)
            val refreshToken = secureStorage.get(SecureStorage.KEY_REFRESH_TOKEN)

            if (accessToken != null && refreshToken != null) {
                Bark.v("Found stored session, attempting to restore")

                // Restore session
                authService.setSession(accessToken, refreshToken)

                // Get current session to verify it's valid
                val session = authService.getCurrentSession()

                if (session != null) {
                    val user = session.user?.toDomain()
                    if (user != null) {
                        updateAuthState(user)
                        Bark.d("Session restored successfully for user: ${user.email}")
                        return Result.success(user)
                    }
                }

                // Session invalid, clear storage
                Bark.w("Stored session was invalid, clearing")
                clearStoredSession()
            } else {
                Bark.v("No stored session found")
            }

            Result.success(null)
        } catch (e: Exception) {
            Bark.e("Failed to initialize auth", e)
            clearStoredSession()
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<User> {
        Bark.v("Signing up with email: $email")

        return try {
            val session = authService.signUpWithEmail(email, password)
            val user = session.user?.toDomain()
                ?: throw IllegalStateException("Sign up succeeded but user info is missing")

            // Store session tokens
            storeSession(session.accessToken, session.refreshToken)

            // Update state
            updateAuthState(user)

            Bark.d("Sign up successful for: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Bark.e("Sign up failed for: $email", e)
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        Bark.v("Signing in with email: $email")

        return try {
            val session = authService.signInWithEmail(email, password)
            val user = session.user?.toDomain()
                ?: throw IllegalStateException("Sign in succeeded but user info is missing")

            // Store session tokens
            storeSession(session.accessToken, session.refreshToken)

            // Update state
            updateAuthState(user)

            Bark.d("Sign in successful for: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Bark.e("Sign in failed for: $email", e)
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun signInWithDiscord(): Result<String> {
        Bark.v("Initiating Discord OAuth")

        return try {
            val url = authService.getOAuthUrl("discord")
            Bark.d("Discord OAuth URL generated")
            Result.success(url)
        } catch (e: Exception) {
            Bark.e("Failed to get Discord OAuth URL", e)
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun signInWithGoogle(): Result<String> {
        Bark.v("Initiating Google OAuth")

        return try {
            val url = authService.getOAuthUrl("google")
            Bark.d("Google OAuth URL generated")
            Result.success(url)
        } catch (e: Exception) {
            Bark.e("Failed to get Google OAuth URL", e)
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun handleOAuthCallback(callbackUrl: String): Result<User> {
        Bark.v("Handling OAuth callback")

        return try {
            val session = authService.handleOAuthCallback(callbackUrl)
            val user = session.user?.toDomain()
                ?: throw IllegalStateException("OAuth succeeded but user info is missing")

            // Store session tokens
            storeSession(session.accessToken, session.refreshToken)

            // Update state
            updateAuthState(user)

            Bark.d("OAuth sign in successful for: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Bark.e("OAuth callback failed", e)
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun signOut(): Result<Unit> {
        Bark.v("Signing out user: ${_currentUser.value?.email}")

        return try {
            // Sign out from Supabase
            authService.signOut()

            // Clear stored session
            clearStoredSession()

            // Update state
            updateAuthState(null)

            Bark.d("Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Bark.e("Sign out failed", e)
            // Still clear local state even if server sign out fails
            clearStoredSession()
            updateAuthState(null)
            Result.failure(e.toAuthError())
        }
    }

    override suspend fun refreshSession(): Result<User> {
        Bark.v("Refreshing session")

        return try {
            val session = authService.refreshSession()
            val user = session.user?.toDomain()
                ?: throw IllegalStateException("Session refresh succeeded but user info is missing")

            // Store new tokens
            storeSession(session.accessToken, session.refreshToken)

            // Update state
            updateAuthState(user)

            Bark.d("Session refresh successful")
            Result.success(user)
        } catch (e: Exception) {
            Bark.e("Session refresh failed", e)
            // If refresh fails, user needs to sign in again
            clearStoredSession()
            updateAuthState(null)
            Result.failure(e.toAuthError())
        }
    }

    /**
     * Stores session tokens in secure storage.
     */
    private fun storeSession(accessToken: String, refreshToken: String) {
        secureStorage.save(SecureStorage.KEY_ACCESS_TOKEN, accessToken)
        secureStorage.save(SecureStorage.KEY_REFRESH_TOKEN, refreshToken)
        Bark.v("Session tokens stored securely")
    }

    /**
     * Clears stored session tokens.
     */
    private fun clearStoredSession() {
        secureStorage.remove(SecureStorage.KEY_ACCESS_TOKEN)
        secureStorage.remove(SecureStorage.KEY_REFRESH_TOKEN)
        secureStorage.remove(SecureStorage.KEY_USER_ID)
        Bark.v("Session tokens cleared")
    }

    /**
     * Updates auth state flows.
     */
    private fun updateAuthState(user: User?) {
        _currentUser.value = user
        _isAuthenticated.value = user != null

        // Optionally store user ID for quick access
        if (user != null) {
            secureStorage.save(SecureStorage.KEY_USER_ID, user.id)
        }
    }
}