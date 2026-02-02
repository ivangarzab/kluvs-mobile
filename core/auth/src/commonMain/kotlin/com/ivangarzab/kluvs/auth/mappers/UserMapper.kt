package com.ivangarzab.kluvs.auth.mappers

import com.ivangarzab.kluvs.model.AuthProvider
import com.ivangarzab.kluvs.model.User
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.jsonPrimitive

/**
 * Maps Supabase UserInfo to domain User model.
 */
fun UserInfo.toDomain(): User {
    // Supabase stores provider in app_metadata or user_metadata
    val providerStr = userMetadata?.get("provider")?.jsonPrimitive?.content
        ?: appMetadata?.get("provider")?.jsonPrimitive?.content
        ?: "email"

    val provider = AuthProvider.fromString(providerStr)

    // Extract display name from metadata (varies by provider)
    val displayName = userMetadata?.get("full_name")?.jsonPrimitive?.content
        ?: userMetadata?.get("name")?.jsonPrimitive?.content
        ?: userMetadata?.get("user_name")?.jsonPrimitive?.content
        ?: email?.substringBefore("@") // Fallback to email username

    // Extract avatar URL from metadata
    val avatarUrl = userMetadata?.get("avatar_url")?.jsonPrimitive?.content
        ?: userMetadata?.get("picture")?.jsonPrimitive?.content

    return User(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        provider = provider
    )
}