package com.ivangarzab.kluvs.data.remote.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for IntListToStringListSerializer and NullableIntListToStringListSerializer.
 *
 * These serializers handle conversion between JSON arrays of numbers/strings and Kotlin List<String>,
 * which is necessary for shame_list and similar fields that contain member IDs.
 */
class IntListToStringListSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    // Test data classes
    @Serializable
    data class TestDto(
        @Serializable(with = IntListToStringListSerializer::class)
        val ids: List<String>
    )

    @Serializable
    data class NullableTestDto(
        @Serializable(with = NullableIntListToStringListSerializer::class)
        val ids: List<String>? = null
    )

    // ========================================
    // IntListToStringListSerializer Tests
    // ========================================

    @Test
    fun testDeserializeFromJsonNumberArray() {
        // Given: JSON with array of numbers
        val jsonString = """{"ids":[1,2,3]}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should convert to list of strings
        assertEquals(listOf("1", "2", "3"), result.ids)
    }

    @Test
    fun testDeserializeFromJsonStringArray() {
        // Given: JSON with array of strings
        val jsonString = """{"ids":["10","20","30"]}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should keep as list of strings
        assertEquals(listOf("10", "20", "30"), result.ids)
    }

    @Test
    fun testDeserializeFromMixedArray() {
        // Given: JSON with mixed array (numbers and strings)
        val jsonString = """{"ids":[1,"2",3]}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should convert all to strings
        assertEquals(listOf("1", "2", "3"), result.ids)
    }

    @Test
    fun testDeserializeEmptyArray() {
        // Given: JSON with empty array
        val jsonString = """{"ids":[]}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should be empty list
        assertTrue(result.ids.isEmpty())
    }

    @Test
    fun testDeserializeSingleElement() {
        // Given: JSON with single element array
        val jsonString = """{"ids":[42]}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should have one element
        assertEquals(listOf("42"), result.ids)
    }

    @Test
    fun testDeserializeLargeNumbers() {
        // Given: JSON with large numbers
        val jsonString = """{"ids":[2147483647,999999999]}"""

        // When: deserializing
        val result = json.decodeFromString<TestDto>(jsonString)

        // Then: should convert to strings
        assertEquals(listOf("2147483647", "999999999"), result.ids)
    }

    @Test
    fun testSerializeToJsonNumberArray() {
        // Given: DTO with list of string ids
        val dto = TestDto(ids = listOf("4", "5", "6"))

        // When: serializing
        val jsonString = json.encodeToString(TestDto.serializer(), dto)

        // Then: should serialize as array of numbers (for backward compatibility)
        assertEquals("""{"ids":[4,5,6]}""", jsonString)
    }

    @Test
    fun testSerializeEmptyList() {
        // Given: DTO with empty list
        val dto = TestDto(ids = emptyList())

        // When: serializing
        val jsonString = json.encodeToString(TestDto.serializer(), dto)

        // Then: should serialize as empty array
        assertEquals("""{"ids":[]}""", jsonString)
    }

    @Test
    fun testSerializeSingleElement() {
        // Given: DTO with single element list
        val dto = TestDto(ids = listOf("7"))

        // When: serializing
        val jsonString = json.encodeToString(TestDto.serializer(), dto)

        // Then: should serialize as single element array
        assertEquals("""{"ids":[7]}""", jsonString)
    }

    @Test
    fun testRoundTripFromNumbers() {
        // Given: JSON with number array
        val originalJson = """{"ids":[100,200,300]}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<TestDto>(originalJson)
        val resultJson = json.encodeToString(TestDto.serializer(), dto)

        // Then: should maintain the values
        assertEquals(listOf("100", "200", "300"), dto.ids)
        assertEquals("""{"ids":[100,200,300]}""", resultJson)
    }

    @Test
    fun testRoundTripFromStrings() {
        // Given: JSON with string array
        val originalJson = """{"ids":["111","222","333"]}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<TestDto>(originalJson)
        val resultJson = json.encodeToString(TestDto.serializer(), dto)

        // Then: should maintain the values
        assertEquals(listOf("111", "222", "333"), dto.ids)
        assertEquals("""{"ids":[111,222,333]}""", resultJson)
    }

    // ========================================
    // NullableIntListToStringListSerializer Tests
    // ========================================

    @Test
    fun testNullableDeserializeFromJsonNumberArray() {
        // Given: JSON with array of numbers
        val jsonString = """{"ids":[1,2,3]}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should convert to list of strings
        assertEquals(listOf("1", "2", "3"), result.ids)
    }

    @Test
    fun testNullableDeserializeFromJsonStringArray() {
        // Given: JSON with array of strings
        val jsonString = """{"ids":["10","20","30"]}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should keep as list of strings
        assertEquals(listOf("10", "20", "30"), result.ids)
    }

    @Test
    fun testNullableDeserializeNull() {
        // Given: JSON with null
        val jsonString = """{"ids":null}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should be null
        assertEquals(null, result.ids)
    }

    @Test
    fun testNullableDeserializeMissingField() {
        // Given: JSON without ids field
        val jsonString = """{}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should be null (default value)
        assertEquals(null, result.ids)
    }

    @Test
    fun testNullableDeserializeEmptyArray() {
        // Given: JSON with empty array
        val jsonString = """{"ids":[]}"""

        // When: deserializing
        val result = json.decodeFromString<NullableTestDto>(jsonString)

        // Then: should be empty list (not null)
        assertTrue(result.ids?.isEmpty() == true)
    }

    @Test
    fun testNullableSerializeNonNull() {
        // Given: DTO with non-null list
        val dto = NullableTestDto(ids = listOf("4", "5", "6"))

        // When: serializing
        val jsonString = json.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should serialize as array of numbers
        assertEquals("""{"ids":[4,5,6]}""", jsonString)
    }

    @Test
    fun testNullableSerializeNull() {
        // Given: DTO with null list (which matches the default value)
        val dto = NullableTestDto(ids = null)

        // When: serializing with explicit nulls and encode defaults
        val jsonWithExplicitNulls = Json {
            explicitNulls = true
            encodeDefaults = true
        }
        val jsonString = jsonWithExplicitNulls.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should serialize as null (when encodeDefaults is true)
        assertEquals("""{"ids":null}""", jsonString)
    }

    @Test
    fun testNullableSerializeEmptyList() {
        // Given: DTO with empty list
        val dto = NullableTestDto(ids = emptyList())

        // When: serializing
        val jsonString = json.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should serialize as empty array
        assertEquals("""{"ids":[]}""", jsonString)
    }

    @Test
    fun testNullableRoundTripWithValue() {
        // Given: JSON with number array
        val originalJson = """{"ids":[100,200,300]}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<NullableTestDto>(originalJson)
        val resultJson = json.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should maintain the values
        assertEquals(listOf("100", "200", "300"), dto.ids)
        assertEquals("""{"ids":[100,200,300]}""", resultJson)
    }

    @Test
    fun testNullableRoundTripWithNull() {
        // Given: JSON with null
        val originalJson = """{"ids":null}"""

        // When: deserializing and serializing with explicit nulls and encode defaults
        val jsonWithExplicitNulls = Json {
            explicitNulls = true
            encodeDefaults = true
        }
        val dto = jsonWithExplicitNulls.decodeFromString<NullableTestDto>(originalJson)
        val resultJson = jsonWithExplicitNulls.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should maintain null
        assertEquals(null, dto.ids)
        assertEquals("""{"ids":null}""", resultJson)
    }

    @Test
    fun testNullableRoundTripWithEmptyArray() {
        // Given: JSON with empty array
        val originalJson = """{"ids":[]}"""

        // When: deserializing and serializing
        val dto = json.decodeFromString<NullableTestDto>(originalJson)
        val resultJson = json.encodeToString(NullableTestDto.serializer(), dto)

        // Then: should maintain empty array
        assertTrue(dto.ids?.isEmpty() == true)
        assertEquals("""{"ids":[]}""", resultJson)
    }
}
