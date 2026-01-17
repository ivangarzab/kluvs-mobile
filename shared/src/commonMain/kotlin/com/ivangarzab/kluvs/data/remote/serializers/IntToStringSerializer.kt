package com.ivangarzab.kluvs.data.remote.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

/**
 * Custom serializer for auto-increment integer IDs (non-nullable).
 * Accepts both JSON numbers and strings, always returns String in Kotlin.
 */
object IntToStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: String) {
        // Send as integer for backward compatibility with API
        encoder.encodeInt(value.toInt())
    }

    override fun deserialize(decoder: Decoder): String {
        // Handle both JSON numbers and strings
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                // Try as int first, then as string
                element.intOrNull?.toString() ?: element.content
            } else {
                throw IllegalArgumentException("Expected JsonPrimitive but got ${element::class}")
            }
        } else {
            // Non-JSON decoder, try int first
            try {
                decoder.decodeInt().toString()
            } catch (e: Exception) {
                decoder.decodeString()
            }
        }
    }
}

/**
 * Custom serializer for auto-increment integer IDs (nullable).
 * Accepts both JSON numbers and strings, always returns String? in Kotlin.
 */
@OptIn(ExperimentalSerializationApi::class)
object NullableIntToStringSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            // Send as integer for backward compatibility with API
            encoder.encodeInt(value.toInt())
        }
    }

    override fun deserialize(decoder: Decoder): String? {
        // Handle both JSON numbers and strings
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            if (element is JsonPrimitive) {
                if (element.isString && element.content == "null") {
                    null
                } else {
                    // Try as int first, then as string
                    element.intOrNull?.toString() ?: element.content
                }
            } else {
                null
            }
        } else {
            // Non-JSON decoder, try int first
            try {
                decoder.decodeInt().toString()
            } catch (e: Exception) {
                try {
                    decoder.decodeString()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
