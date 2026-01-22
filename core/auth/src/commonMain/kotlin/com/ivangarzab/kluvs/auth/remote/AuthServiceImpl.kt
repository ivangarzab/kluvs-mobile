package com.ivangarzab.kluvs.auth.remote

import com.ivangarzab.bark.Bark
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Discord
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.ktor.http.Url
import io.ktor.http.parseQueryString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Implementation of [AuthService] using Supabase GoTrue.
 */
class AuthServiceImpl(
    private val supabaseClient: SupabaseClient
) : AuthService {

    private val auth: Auth
        get() = supabaseClient.auth

    override suspend fun signUpWithEmail(email: String, password: String): UserSession {
        Bark.v("Signing up user with email: $email")

        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // After sign up, user is automatically signed in
            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Sign up succeeded but no session was created")

            Bark.d("Sign up successful for email: $email")
            session
        } catch (e: Exception) {
            Bark.e("Sign up failed for email: $email", e)
            throw e
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): UserSession {
        Bark.v("Signing in user with email: $email")

        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Sign in succeeded but no session was created")

            Bark.d("Sign in successful for email: $email")
            session
        } catch (e: Exception) {
            Bark.e("Sign in failed for email: $email", e)
            throw e
        }
    }

    override suspend fun getOAuthUrl(provider: String): String {
        Bark.v("Getting OAuth URL for provider: $provider")

        return try {
            val oAuthProvider = when (provider.lowercase()) {
                "discord" -> Discord
                "google" -> Google
                "apple" -> Apple
                else -> throw IllegalArgumentException("Unknown OAuth provider: $provider")
            }

            val url = auth.getOAuthUrl(oAuthProvider, redirectUrl = REDIRECT_URL)

            Bark.v("Generated OAuth URL for $provider")
            url
        } catch (e: Exception) {
            Bark.e("Failed to get OAuth URL for $provider", e)
            throw e
        }
    }

    override suspend fun handleOAuthCallback(url: String): UserSession {
        Bark.d("Handling OAuth callback URL: $url")

        return try {
            // Parse the callback URL to extract tokens from the fragment
            // Supabase OAuth callbacks use the fragment (after #) for token data
            val parsedUrl = Url(url)
            val fragment = parsedUrl.fragment

            if (fragment.isBlank()) {
                Bark.w("OAuth callback URL missing fragment. Full URL: $url")
                throw IllegalArgumentException("OAuth callback URL missing fragment with tokens")
            }

            // Parse fragment parameters (format: access_token=...&refresh_token=...&...)
            val params = parseQueryString(fragment)
            val accessToken = params["access_token"]
                ?: throw IllegalArgumentException("Missing access_token in OAuth callback")
            val refreshToken = params["refresh_token"]
                ?: throw IllegalArgumentException("Missing refresh_token in OAuth callback")

            Bark.v("Parsed OAuth tokens from callback")

            // Import the tokens and retrieve user data to establish complete session
            auth.importAuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken,
                retrieveUser = true
            )

            // Wait for session status to confirm authentication (with timeout)
            // This handles the race condition where currentSessionOrNull() returns null
            // before the session is fully established internally
            Bark.v("Waiting for session status to confirm authentication...")
            val sessionResult = withTimeoutOrNull(5000L) {
                auth.sessionStatus.first { status ->
                    Bark.v("Session status update: $status")
                    status is SessionStatus.Authenticated
                }
            }

            if (sessionResult == null) {
                Bark.e("Session status timeout: did not become Authenticated within 5 seconds")
                throw IllegalStateException("OAuth timeout: session not established")
            }

            val userSession = auth.currentSessionOrNull()
                ?: throw IllegalStateException("OAuth succeeded but no session was created")

            Bark.d("OAuth sign in successful")
            userSession
        } catch (e: Exception) {
            Bark.e("Failed to handle OAuth callback", e)
            throw e
        }
    }

    override suspend fun signInWithAppleIdToken(idToken: String): UserSession {
        Bark.v("Signing in with Apple ID token")

        return try {
            auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Apple
            }

            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Apple Sign In succeeded but no session was created")

            Bark.d("Apple Sign In successful")
            session
        } catch (e: Exception) {
            Bark.e("Apple Sign In failed", e)
            throw e
        }
    }

    override suspend fun signOut() {
        Bark.v("Signing out current user")

        try {
            auth.signOut()

            // Wait for session status to confirm sign out (with timeout)
            // This ensures the session is fully cleared before returning
            Bark.v("Waiting for session status to confirm sign out...")
            val signOutResult = withTimeoutOrNull(3000L) {
                auth.sessionStatus.first { status ->
                    Bark.v("Session status update during sign out: $status")
                    status is SessionStatus.NotAuthenticated
                }
            }

            if (signOutResult == null) {
                Bark.w("Sign out: session status did not become NotAuthenticated within 3 seconds, continuing anyway")
            }

            Bark.v("Sign out successful")
        } catch (e: Exception) {
            Bark.e("Sign out failed", e)
            throw e
        }
    }

    override suspend fun getCurrentSession(): UserSession? {
        return auth.currentSessionOrNull()
    }

    override suspend fun refreshSession(): UserSession {
        Bark.v("Refreshing session")

        return try {
            auth.refreshCurrentSession()
            val session = auth.currentSessionOrNull()
                ?: throw IllegalStateException("Session refresh succeeded but no session exists")

            Bark.d("Session refresh successful")
            session
        } catch (e: Exception) {
            Bark.e("Session refresh failed", e)
            throw e
        }
    }

    override suspend fun setSession(accessToken: String, refreshToken: String) {
        Bark.v("Restoring session from stored tokens")

        try {
            auth.importAuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            Bark.d("Session restored successfully")
        } catch (e: Exception) {
            Bark.e("Failed to restore session", e)
            throw e
        }
    }

    companion object {
        const val REDIRECT_URL = "kluvs://auth/callback"
    }
}