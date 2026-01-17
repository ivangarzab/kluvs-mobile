package com.ivangarzab.kluvs.data.local.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for IosSecureStorage.
 *
 * These tests verify the iOS Keychain implementation works correctly,
 * including memory management and edge cases.
 */
class IosSecureStorageTest {

    @Test
    fun `save and get returns same value`() {
        // Given
        val storage = IosSecureStorage()
        val key = "test_key"
        val value = "test_value"

        // When
        storage.save(key, value)
        val retrieved = storage.get(key)

        // Then
        assertNotNull(retrieved, "Retrieved value should not be null")
        assertEquals(value, retrieved, "Retrieved value should match saved value")

        // Cleanup
        storage.remove(key)
    }

    @Test
    fun `get non-existent key returns null`() {
        // Given
        val storage = IosSecureStorage()
        val nonExistentKey = "non_existent_key_12345"

        // When
        val result = storage.get(nonExistentKey)

        // Then
        assertNull(result, "Non-existent key should return null")
    }

    @Test
    fun `save overwrites existing value`() {
        // Given
        val storage = IosSecureStorage()
        val key = "overwrite_key"
        val originalValue = "original_value"
        val newValue = "new_value"

        // When
        storage.save(key, originalValue)
        storage.save(key, newValue)
        val retrieved = storage.get(key)

        // Then
        assertEquals(newValue, retrieved, "Should retrieve the new value, not the original")

        // Cleanup
        storage.remove(key)
    }

    @Test
    fun `remove deletes value`() {
        // Given
        val storage = IosSecureStorage()
        val key = "remove_key"
        val value = "value_to_remove"

        // When
        storage.save(key, value)
        storage.remove(key)
        val retrieved = storage.get(key)

        // Then
        assertNull(retrieved, "Removed key should return null")
    }

    @Test
    fun `remove non-existent key does not throw`() {
        // Given
        val storage = IosSecureStorage()
        val nonExistentKey = "non_existent_remove_key"

        // When/Then - should not throw
        storage.remove(nonExistentKey)
    }

    @Test
    fun `clear removes all values for service`() {
        // Given
        val storage = IosSecureStorage()
        val key1 = "clear_test_key1"
        val key2 = "clear_test_key2"
        val key3 = "clear_test_key3"

        storage.save(key1, "value1")
        storage.save(key2, "value2")
        storage.save(key3, "value3")

        // When
        storage.clear()

        // Then
        assertNull(storage.get(key1), "Key1 should be cleared")
        assertNull(storage.get(key2), "Key2 should be cleared")
        assertNull(storage.get(key3), "Key3 should be cleared")
    }

    @Test
    fun `multiple keys do not interfere with each other`() {
        // Given
        val storage = IosSecureStorage()
        val key1 = "multi_key1"
        val key2 = "multi_key2"
        val value1 = "value1"
        val value2 = "value2"

        // When
        storage.save(key1, value1)
        storage.save(key2, value2)

        // Then
        assertEquals(value1, storage.get(key1), "Key1 should have its own value")
        assertEquals(value2, storage.get(key2), "Key2 should have its own value")

        // Cleanup
        storage.remove(key1)
        storage.remove(key2)
    }

    @Test
    fun `save handles special characters and long strings`() {
        // Given
        val storage = IosSecureStorage()
        val key = "special_chars_key"
        val specialValue = "Value with special chars: !@#\$%^&*()_+-=[]{}|;':\",./<>?\nNewline\tTab"

        // When
        storage.save(key, specialValue)
        val retrieved = storage.get(key)

        // Then
        assertEquals(specialValue, retrieved, "Special characters should be preserved")

        // Cleanup
        storage.remove(key)
    }

    @Test
    fun `save handles long token-like strings`() {
        // Given
        val storage = IosSecureStorage()
        val key = SecureStorage.KEY_ACCESS_TOKEN
        val longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

        // When
        storage.save(key, longToken)
        val retrieved = storage.get(key)

        // Then
        assertEquals(longToken, retrieved, "Long token-like strings should be preserved")

        // Cleanup
        storage.remove(key)
    }
}
