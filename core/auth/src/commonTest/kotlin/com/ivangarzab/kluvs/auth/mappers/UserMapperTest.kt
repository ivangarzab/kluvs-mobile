package com.ivangarzab.kluvs.auth.mappers

import com.ivangarzab.kluvs.model.AuthProvider
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for UserMapper.kt
 *
 * Tests the mapping logic from Supabase UserInfo to domain User model,
 * including all fallback chains for provider, display name, and avatar URL extraction.
 */
@OptIn(SupabaseInternal::class)
class UserMapperTest {

    /**
     * Helper function to create test UserInfo instances.
     * Uses the UserInfo constructor directly with JsonObject metadata.
     */
    @OptIn(kotlin.time.ExperimentalTime::class)
    private fun createUserInfo(
        id: String = "test-user-id",
        email: String? = "test@example.com",
        userMetadata: Map<String, String>? = null,
        appMetadata: Map<String, String>? = null
    ): UserInfo {
        val userMetadataJson = userMetadata?.let { buildJsonObject {
            it.forEach { (key, value) -> put(key, value) }
        } }

        val appMetadataJson = appMetadata?.let { buildJsonObject {
            it.forEach { (key, value) -> put(key, value) }
        } }

        return UserInfo(
            id = id,
            email = email,
            phone = null,
            emailConfirmedAt = null,
            phoneConfirmedAt = null,
            createdAt = null,
            updatedAt = null,
            lastSignInAt = null,
            appMetadata = appMetadataJson,
            userMetadata = userMetadataJson,
            aud = "",
            confirmationSentAt = null,
            recoverySentAt = null,
            invitedAt = null,
            actionLink = null,
            role = null,
            factors = emptyList()
        )
    }

    // ========== Provider Extraction Tests ==========

    @Test
    fun `provider extracted from userMetadata when present`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = mapOf("provider" to "discord")
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals(AuthProvider.DISCORD, user.provider)
    }

    @Test
    fun `provider falls back to appMetadata when not in userMetadata`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = emptyMap(),
            appMetadata = mapOf("provider" to "google")
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals(AuthProvider.GOOGLE, user.provider)
    }

    @Test
    fun `provider defaults to email when not in any metadata`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = emptyMap(),
            appMetadata = emptyMap()
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals(AuthProvider.EMAIL, user.provider)
    }

    @Test
    fun `provider defaults to email when metadata maps are null`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = null,
            appMetadata = null
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals(AuthProvider.EMAIL, user.provider)
    }

    @Test
    fun `provider supports all provider types via AuthProvider fromString`() {
        // Test each known provider type
        val providers = listOf(
            "email" to AuthProvider.EMAIL,
            "discord" to AuthProvider.DISCORD,
            "google" to AuthProvider.GOOGLE,
            "apple" to AuthProvider.APPLE
        )

        providers.forEach { (providerString, expectedProvider) ->
            // Given
            val userInfo = createUserInfo(
                id = "user-123",
                email = "test@example.com",
                userMetadata = mapOf("provider" to providerString)
            )

            // When
            val user = userInfo.toDomain()

            // Then
            assertEquals(expectedProvider, user.provider, "Provider '$providerString' should map to $expectedProvider")
        }
    }

    // ========== Display Name Extraction Tests ==========

    @Test
    fun `displayName extracted from full_name when present`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = mapOf(
                "full_name" to "John Doe",
                "name" to "Johnny",
                "user_name" to "johndoe123"
            )
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("John Doe", user.displayName)
    }

    @Test
    fun `displayName falls back to name when full_name absent`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = mapOf(
                "name" to "Johnny",
                "user_name" to "johndoe123"
            )
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("Johnny", user.displayName)
    }

    @Test
    fun `displayName falls back to user_name when full_name and name absent`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = mapOf("user_name" to "johndoe123")
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("johndoe123", user.displayName)
    }

    @Test
    fun `displayName falls back to email prefix when all metadata fields absent`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "john.doe@example.com",
            userMetadata = emptyMap()
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("john.doe", user.displayName)
    }

    @Test
    fun `displayName is null when email is null and metadata absent`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = null,
            userMetadata = emptyMap()
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertNull(user.displayName)
    }

    @Test
    fun `displayName is null when email is null and userMetadata is null`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = null,
            userMetadata = null
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertNull(user.displayName)
    }

    // ========== Avatar URL Extraction Tests ==========

    @Test
    fun `avatarUrl extracted from avatar_url when present`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = mapOf(
                "avatar_url" to "https://example.com/avatar.jpg",
                "picture" to "https://example.com/picture.jpg"
            )
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("https://example.com/avatar.jpg", user.avatarUrl)
    }

    @Test
    fun `avatarUrl falls back to picture when avatar_url absent`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = mapOf("picture" to "https://example.com/picture.jpg")
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("https://example.com/picture.jpg", user.avatarUrl)
    }

    @Test
    fun `avatarUrl is null when neither avatar_url nor picture present`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = emptyMap()
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertNull(user.avatarUrl)
    }

    @Test
    fun `avatarUrl is null when userMetadata is null`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-123",
            email = "test@example.com",
            userMetadata = null
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertNull(user.avatarUrl)
    }

    // ========== Complete Mapping Tests ==========

    @Test
    fun `complete mapping with full metadata`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-456",
            email = "alice@example.com",
            userMetadata = mapOf(
                "provider" to "google",
                "full_name" to "Alice Smith",
                "avatar_url" to "https://google.com/avatar/alice.jpg"
            )
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("user-456", user.id)
        assertEquals("alice@example.com", user.email)
        assertEquals("Alice Smith", user.displayName)
        assertEquals("https://google.com/avatar/alice.jpg", user.avatarUrl)
        assertEquals(AuthProvider.GOOGLE, user.provider)
    }

    @Test
    fun `minimal mapping with only required fields`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-789",
            email = "minimal@example.com",
            userMetadata = null,
            appMetadata = null
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("user-789", user.id)
        assertEquals("minimal@example.com", user.email)
        assertEquals("minimal", user.displayName) // Falls back to email prefix
        assertNull(user.avatarUrl)
        assertEquals(AuthProvider.EMAIL, user.provider) // Defaults to email
    }

    @Test
    fun `mapping with null email`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-xyz",
            email = null,
            userMetadata = mapOf(
                "provider" to "discord",
                "user_name" to "discorduser#1234",
                "avatar_url" to "https://cdn.discordapp.com/avatars/xyz.png"
            )
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("user-xyz", user.id)
        assertNull(user.email)
        assertEquals("discorduser#1234", user.displayName)
        assertEquals("https://cdn.discordapp.com/avatars/xyz.png", user.avatarUrl)
        assertEquals(AuthProvider.DISCORD, user.provider)
    }

    @Test
    fun `mapping handles mixed metadata sources`() {
        // Given: provider in appMetadata, display name in userMetadata
        val userInfo = createUserInfo(
            id = "user-mixed",
            email = "mixed@example.com",
            userMetadata = mapOf(
                "name" to "Mixed User",
                "picture" to "https://example.com/pic.png"
            ),
            appMetadata = mapOf("provider" to "apple")
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("user-mixed", user.id)
        assertEquals("mixed@example.com", user.email)
        assertEquals("Mixed User", user.displayName)
        assertEquals("https://example.com/pic.png", user.avatarUrl)
        assertEquals(AuthProvider.APPLE, user.provider)
    }

    // ========== Edge Cases ==========

    @Test
    fun `mapping handles empty string values in metadata`() {
        // Given
        val userInfo = createUserInfo(
            id = "user-empty",
            email = "empty@example.com",
            userMetadata = mapOf(
                "full_name" to "",  // Empty string should still be used (not null)
                "avatar_url" to ""
            )
        )

        // When
        val user = userInfo.toDomain()

        // Then
        assertEquals("", user.displayName) // Empty string is still extracted
        assertEquals("", user.avatarUrl)
    }
}
