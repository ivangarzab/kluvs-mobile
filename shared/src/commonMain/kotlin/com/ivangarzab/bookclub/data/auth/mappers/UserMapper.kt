package com.ivangarzab.bookclub.data.auth.mappers

import com.ivangarzab.bookclub.domain.models.AuthProvider
import com.ivangarzab.bookclub.domain.models.User
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Maps Supabase UserInfo to domain User model.
 */
fun UserInfo.toDomain(): User {
    // Supabase stores provider in app_metadata or user_metadata
    val providerStr = (userMetadata?.get("provider") as? String)
        ?: (appMetadata?.get("provider") as? String)
        ?: "email"

    val provider = AuthProvider.fromString(providerStr)

    // Extract display name from metadata (varies by provider)
    val displayName = (userMetadata?.get("full_name") as? String)
        ?: (userMetadata?.get("name") as? String)
        ?: (userMetadata?.get("user_name") as? String)
        ?: email?.substringBefore("@") // Fallback to email username

    // Extract avatar URL from metadata
    val avatarUrl = (userMetadata?.get("avatar_url") as? String)
        ?: (userMetadata?.get("picture") as? String)

    return User(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        provider = provider
    )
}