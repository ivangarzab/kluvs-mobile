package com.ivangarzab.kluvs.network.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

/**
 * Serializer for List<Int> to List<String> conversion (non-nullable).
 * Accepts both JSON arrays of numbers and strings, always returns List<String> in Kotlin.
 * Used for shame_list which contains member IDs.
 */
object IntListToStringListSerializer : KSerializer<List<String>> {
    private val intListSerializer = ListSerializer(Int.serializer())
    private val stringListSerializer = ListSerializer(String.serializer())

    override val descriptor: SerialDescriptor = stringListSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<String>) {
        // Send as integers for backward compatibility with API
        val intList = value.map { it.toInt() }
        encoder.encodeSerializableValue(intListSerializer, intList)
    }

    override fun deserialize(decoder: Decoder): List<String> {
        // Handle both JSON arrays of numbers and strings
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonArray) {
                element.map { item ->
                    if (item is JsonPrimitive) {
                        // Try as int first, then as string
                        item.intOrNull?.toString() ?: item.content
                    } else {
                        throw IllegalArgumentException("Expected JsonPrimitive but got ${item::class}")
                    }
                }
            } else {
                throw IllegalArgumentException("Expected JsonArray but got ${element::class}")
            }
        } else {
            // Non-JSON decoder, try int list first
            try {
                val intList = decoder.decodeSerializableValue(intListSerializer)
                intList.map { it.toString() }
            } catch (e: Exception) {
                decoder.decodeSerializableValue(stringListSerializer)
            }
        }
    }
}

/**
 * Serializer for List<Int>? to List<String>? conversion (nullable).
 * Accepts both JSON arrays of numbers and strings, always returns List<String>? in Kotlin.
 */
object NullableIntListToStringListSerializer : KSerializer<List<String>?> {
    private val intListSerializer = ListSerializer(Int.serializer()).nullable
    private val stringListSerializer = ListSerializer(String.serializer()).nullable

    override val descriptor: SerialDescriptor = stringListSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<String>?) {
        if (value == null) {
            encoder.encodeSerializableValue(stringListSerializer, null)
        } else {
            // Send as integers for backward compatibility with API
            val intList = value.map { it.toInt() }
            encoder.encodeSerializableValue(intListSerializer, intList)
        }
    }

    override fun deserialize(decoder: Decoder): List<String>? {
        // Handle both JSON arrays of numbers and strings
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonArray) {
                element.map { item ->
                    if (item is JsonPrimitive) {
                        // Try as int first, then as string
                        item.intOrNull?.toString() ?: item.content
                    } else {
                        throw IllegalArgumentException("Expected JsonPrimitive but got ${item::class}")
                    }
                }
            } else {
                null
            }
        } else {
            // Non-JSON decoder, try int list first
            try {
                val intList = decoder.decodeSerializableValue(intListSerializer)
                intList?.map { it.toString() }
            } catch (e: Exception) {
                decoder.decodeSerializableValue(stringListSerializer)
            }
        }
    }
}
