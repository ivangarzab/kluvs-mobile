package com.ivangarzab.kluvs.network.utils

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for JsonHelper.
 *
 * Tests that the JSON configuration is set up correctly for Supabase service calls.
 */
class JsonHelperTest {

    @Serializable
    data class TestDto(
        val required: String,
        val optional: String? = null,
        val defaultValue: Int = 42
    )

    @Test
    fun testJsonConfigurationExists() {
        // When: getting JSON config
        val json = JsonHelper.getJsonForSupabaseService()

        // Then: should not be null
        assertTrue(json.configuration.ignoreUnknownKeys)
        assertTrue(json.configuration.encodeDefaults)
        assertFalse(json.configuration.explicitNulls)
    }

    @Test
    fun testIgnoreUnknownKeys() {
        // Given: JSON with unknown field
        val jsonString = """{"required":"test","unknown":"field"}"""
        val json = JsonHelper.getJsonForSupabaseService()

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should ignore unknown field and not throw exception
        assertEquals("test", result.required)
    }

    @Test
    fun testEncodeDefaultsTrue() {
        // Given: DTO with default value
        val dto = TestDto(required = "test")
        val json = JsonHelper.getJsonForSupabaseService()

        // When: serializing
        val jsonString = json.encodeToString(dto)

        // Then: should include default value
        assertTrue(jsonString.contains("defaultValue"))
        assertTrue(jsonString.contains("42"))
    }

    @Test
    fun testExplicitNullsFalse() {
        // Given: DTO with null optional field
        val dto = TestDto(required = "test", optional = null)
        val json = JsonHelper.getJsonForSupabaseService()

        // When: serializing
        val jsonString = json.encodeToString(dto)

        // Then: should NOT include null field
        assertFalse(jsonString.contains("optional"))
    }

    @Test
    fun testExplicitNullsFalseWithNonNullValue() {
        // Given: DTO with non-null optional field
        val dto = TestDto(required = "test", optional = "value")
        val json = JsonHelper.getJsonForSupabaseService()

        // When: serializing
        val jsonString = json.encodeToString(dto)

        // Then: SHOULD include the field with value
        assertTrue(jsonString.contains("optional"))
        assertTrue(jsonString.contains("value"))
    }

    @Test
    fun testCompleteSerializationExample() {
        // Given: DTO with mixed fields
        val dto = TestDto(
            required = "hello",
            optional = "world",
            defaultValue = 100
        )
        val json = JsonHelper.getJsonForSupabaseService()

        // When: serializing
        val jsonString = json.encodeToString(dto)

        // Then: should have all fields
        assertTrue(jsonString.contains("required"))
        assertTrue(jsonString.contains("hello"))
        assertTrue(jsonString.contains("optional"))
        assertTrue(jsonString.contains("world"))
        assertTrue(jsonString.contains("defaultValue"))
        assertTrue(jsonString.contains("100"))
    }

    @Test
    fun testCompleteDeserializationExample() {
        // Given: JSON with all fields
        val jsonString = """{"required":"hello","optional":"world","defaultValue":100}"""
        val json = JsonHelper.getJsonForSupabaseService()

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should parse all fields correctly
        assertEquals("hello", result.required)
        assertEquals("world", result.optional)
        assertEquals(100, result.defaultValue)
    }

    @Test
    fun testDeserializationWithMissingOptionalField() {
        // Given: JSON without optional field
        val jsonString = """{"required":"hello"}"""
        val json = JsonHelper.getJsonForSupabaseService()

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should use default values
        assertEquals("hello", result.required)
        assertEquals(null, result.optional)
        assertEquals(42, result.defaultValue)
    }

    @Test
    fun testRoundTripWithNullFields() {
        // Given: DTO with null optional
        val original = TestDto(required = "test", optional = null)
        val json = JsonHelper.getJsonForSupabaseService()

        // When: serializing and deserializing
        val jsonString = json.encodeToString(original)
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should maintain values
        assertEquals(original.required, result.required)
        assertEquals(original.optional, result.optional)
        assertEquals(original.defaultValue, result.defaultValue)
    }

    @Test
    fun testRoundTripWithAllFields() {
        // Given: DTO with all fields populated
        val original = TestDto(required = "test", optional = "value", defaultValue = 99)
        val json = JsonHelper.getJsonForSupabaseService()

        // When: serializing and deserializing
        val jsonString = json.encodeToString(original)
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should maintain all values
        assertEquals(original.required, result.required)
        assertEquals(original.optional, result.optional)
        assertEquals(original.defaultValue, result.defaultValue)
    }
}