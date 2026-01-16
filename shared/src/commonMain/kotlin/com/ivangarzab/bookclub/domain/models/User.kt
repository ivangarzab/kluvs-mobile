package com.ivangarzab.bookclub.domain.models

/**
 * Domain model for authenticated User.
 *
 * Represents a user authenticated via Supabase Auth (email/password, Discord, Google, or Apple).
 * This model is separate from [Member] - a User can be a Member of multiple Clubs.
 */
data class User(

    /** Supabase Auth user ID (UUID) */
    val id: String,

    /** User's email address */
    val email: String?,

    /** User's display name (from OAuth provider or profile) */
    val displayName: String?,

    /** User's avatar/profile picture URL (from OAuth provider) */
    val avatarUrl: String?,

    /** Authentication provider used (email, discord, google, apple) */
    val provider: AuthProvider
)

/**
 * Authentication providers supported by the app.
 */
enum class AuthProvider {
    EMAIL,
    DISCORD,
    GOOGLE,
    APPLE;

    companion object {
        fun fromString(value: String): AuthProvider {
            return when (value.lowercase()) {
                "email" -> EMAIL
                "discord" -> DISCORD
                "google" -> GOOGLE
                "apple" -> APPLE
                else -> EMAIL // Default fallback
            }
        }
    }
}