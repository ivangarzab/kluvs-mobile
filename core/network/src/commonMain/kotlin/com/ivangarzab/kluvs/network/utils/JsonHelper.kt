package com.ivangarzab.kluvs.network.utils

import kotlinx.serialization.json.Json

/**
 * [kotlinx.serialization.json.Json]-related helping functions.
 */
object JsonHelper {

    /**
     * Return a [kotlinx.serialization.json.Json] configured to ignore unknown keys, encode default values, and that
     * explicitly adds null values.
     */
    fun getJsonForSupabaseService(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }
}