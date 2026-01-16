package com.ivangarzab.bookclub.data.local.storage

/**
 * Platform-agnostic interface for secure storage of sensitive data (auth tokens, credentials).
 *
 * Platform implementations:
 * - Android: EncryptedSharedPreferences
 * - iOS: Keychain Services
 */
interface SecureStorage {

    /**
     * Saves a key-value pair securely.
     *
     * @param key The key to store the value under
     * @param value The value to store
     */
    fun save(key: String, value: String)

    /**
     * Retrieves a value by key.
     *
     * @param key The key to retrieve
     * @return The stored value, or null if not found
     */
    fun get(key: String): String?

    /**
     * Removes a specific key-value pair.
     *
     * @param key The key to remove
     */
    fun remove(key: String)

    /**
     * Clears all stored data.
     */
    fun clear()

    companion object {
        // Keys for storing auth tokens
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER_ID = "user_id"
    }
}