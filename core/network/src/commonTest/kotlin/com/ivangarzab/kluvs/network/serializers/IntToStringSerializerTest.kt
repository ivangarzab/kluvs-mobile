package com.ivangarzab.kluvs.network.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for IntToStringSerializer and NullableIntToStringSerializer.
 *
 * These serializers handle conversion between JSON numbers/strings and Kotlin Strings,
 * which is necessary for auto-increment integer IDs from the database.
 */
class IntToStringSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    // Test data classes
    @Serializable
    data class TestDto(
        @Serializable(with = IntToStringSerializer::class)
        val id: String
    )

    @Serializable
    data class NullableTestDto(
        @Serializable(with = NullableIntToStringSerializer::class)
        val id: String? = null
    )

    // ========================================
    // IntToStringSerializer Tests
    // ========================================

    @Test
    fun testDeserializeFromJsonNumber() {
        // Given: JSON with numeric id
        val jsonString = """{"id":42}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should convert to string
        assertEquals("42", result.id)
    }

    @Test
    fun testDeserializeFromJsonString() {
        // Given: JSON with string id
        val jsonString = """{"id":"123"}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should keep as string
        assertEquals("123", result.id)
    }

    @Test
    fun testDeserializeLargeNumber() {
        // Given: JSON with large number
        val jsonString = """{"id":2147483647}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should convert to string
        assertEquals("2147483647", result.id)
    }

    @Test
    fun testSerializeToJsonNumber() {
        // Given: DTO with string id
        val dto = TestDto(id = "456")

        // When: serializing
        val jsonString = json.encodeToString(TestDto.serializer(), dto)

        // Then: should serialize as number (for backward compatibility)
        assertEquals("""{"id":456}""", jsonString)
    }

    @Test
    fun testSerializeZero() {
        // Given: DTO with zero id
        val dto = TestDto(id = "0")

        // When: serializing
        val jsonString = json.encodeToString(TestDto.serializer(), dto)

        // Then: should serialize as zero
        assertEquals("""{"id":0}""", jsonString)
    }

    @Test
    fun testRoundTripFromNumber() {
        // Given: JSON with number
        val originalJson = """{"id":789}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<TestDto>(originalJson)
        val resultJson = json.encodeToString(TestDto.serializer(), dto)

        // Then: should maintain the value
        assertEquals("789", dto.id)
        assertEquals("""{"id":789}""", resultJson)
    }

    @Test
    fun testRoundTripFromString() {
        // Given: JSON with string
        val originalJson = """{"id":"999"}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<TestDto>(originalJson)
        val resultJson = json.encodeToString(TestDto.serializer(), dto)

        // Then: should maintain the value
        assertEquals("999", dto.id)
        assertEquals("""{"id":999}""", resultJson)
    }

    // ========================================
    // NullableIntToStringSerializer Tests
    // ========================================

    @Test
    fun testNullableDeserializeFromJsonNumber() {
        // Given: JSON with numeric id
        val jsonString = """{"id":42}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should convert to string
        assertEquals("42", result.id)
    }

    @Test
    fun testNullableDeserializeFromJsonString() {
        // Given: JSON with string id
        val jsonString = """{"id":"123"}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should keep as string
        assertEquals("123", result.id)
    }

    @Test
    fun testNullableDeserializeNull() {
        // Given: JSON with null id
        val jsonString = """{"id":null}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should be null
        assertEquals(null, result.id)
    }

    @Test
    fun testNullableDeserializeMissingField() {
        // Given: JSON without id field
        val jsonString = """{}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should be null (default value)
        assertEquals(null, result.id)
    }

    @Test
    fun testNullableSerializeNonNull() {
        // Given: DTO with non-null string id
        val dto = NullableTestDto(id = "456")

        // When: serializing
        val jsonString = json.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should serialize as number
        assertEquals("""{"id":456}""", jsonString)
    }

    @Test
    fun testNullableSerializeNull() {
        // Given: DTO with null id (which matches the default value)
        val dto = NullableTestDto(id = null)

        // When: serializing with explicit nulls and encode defaults
        val jsonWithExplicitNulls = Json {
            explicitNulls = true
            encodeDefaults = true
        }
        val jsonString = jsonWithExplicitNulls.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should serialize as null (when encodeDefaults is true)
        assertEquals("""{"id":null}""", jsonString)
    }

    @Test
    fun testNullableRoundTripWithValue() {
        // Given: JSON with number
        val originalJson = """{"id":789}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<NullableTestDto>(originalJson)
        val resultJson = json.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should maintain the value
        assertEquals("789", dto.id)
        assertEquals("""{"id":789}""", resultJson)
    }

    @Test
    fun testNullableRoundTripWithNull() {
        // Given: JSON with null
        val originalJson = """{"id":null}"""

        // When: deserializing and serializing with explicit nulls and encode defaults
        val jsonWithExplicitNulls = Json {
            explicitNulls = true
            encodeDefaults = true
        }
        val dto = jsonWithExplicitNulls.decodeFromString<NullableTestDto>(originalJson)
        val resultJson = jsonWithExplicitNulls.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should maintain null
        assertEquals(null, dto.id)
        assertEquals("""{"id":null}""", resultJson)
    }
}
