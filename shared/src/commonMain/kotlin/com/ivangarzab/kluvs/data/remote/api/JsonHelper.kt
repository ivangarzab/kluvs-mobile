package com.ivangarzab.kluvs.data.remote.api

import kotlinx.serialization.json.Json

/**
 * [Json]-related helping functions.
 */
object JsonHelper {

    /**
     * Return a [Json] configured to ignore unknown keys, encode default values, and that
     * explicitly adds null values.
     */
    fun getJsonForSupabaseService(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }
}