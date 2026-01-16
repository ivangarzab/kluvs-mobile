package com.ivangarzab.bookclub.data.auth

import com.ivangarzab.bark.Bark
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession

/**
 * Implementation of [AuthService] using Supabase GoTrue.
 */
class AuthServiceImpl(
    private val supabaseClient: SupabaseClient
) : AuthService {

    private val auth: Auth
        get() = supabaseClient.auth

    override suspend fun signUpWithEmail(email: String, password: String): UserSession {
        Bark.d("Signing up user with email: $email")

        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // After sign up, user is automatically signed in
            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Sign up succeeded but no session was created")

            Bark.i("Sign up successful for email: $email")
            session
        } catch (e: Exception) {
            Bark.e("Sign up failed for email: $email", e)
            throw e
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): UserSession {
        Bark.d("Signing in user with email: $email")

        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Sign in succeeded but no session was created")

            Bark.i("Sign in successful for email: $email")
            session
        } catch (e: Exception) {
            Bark.e("Sign in failed for email: $email", e)
            throw e
        }
    }

    override suspend fun getOAuthUrl(provider: String): String {
        Bark.d("Getting OAuth URL for provider: $provider")

        // For now, we'll implement this when we add OAuth support
        // This will use Supabase's OAuth URL generation
        TODO("OAuth URL generation will be implemented in OAuth phase")
    }

    override suspend fun handleOAuthCallback(url: String): UserSession {
        Bark.d("Handling OAuth callback")

        // For now, we'll implement this when we add OAuth support
        TODO("OAuth callback handling will be implemented in OAuth phase")
    }

    override suspend fun signOut() {
        Bark.d("Signing out current user")

        try {
            auth.signOut()
            Bark.i("Sign out successful")
        } catch (e: Exception) {
            Bark.e("Sign out failed", e)
            throw e
        }
    }

    override suspend fun getCurrentSession(): UserSession? {
        return auth.currentSessionOrNull()
    }

    override suspend fun refreshSession(): UserSession {
        Bark.d("Refreshing session")

        return try {
            auth.refreshCurrentSession()
            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Session refresh succeeded but no session exists")

            Bark.i("Session refresh successful")
            session
        } catch (e: Exception) {
            Bark.e("Session refresh failed", e)
            throw e
        }
    }

    override suspend fun setSession(accessToken: String, refreshToken: String) {
        Bark.d("Restoring session from stored tokens")

        try {
            auth.importAuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            Bark.i("Session restored successfully")
        } catch (e: Exception) {
            Bark.e("Failed to restore session", e)
            throw e
        }
    }
}