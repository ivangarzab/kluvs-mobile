package com.ivangarzab.kluvs.data.local.cache

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Cache policy for determining data freshness.
 * Uses TTL (time-to-live) to decide if cached data should be refreshed.
 */
@OptIn(ExperimentalTime::class)
class CachePolicy(
    private val clock: Clock = Clock.System
) {
    /**
     * Checks if cached data is stale based on its last fetched timestamp and TTL.
     *
     * @param lastFetchedAt Timestamp in milliseconds when data was last fetched (null = never fetched)
     * @param ttl Time-to-live duration for this data type
     * @return true if data is stale and should be refreshed, false if still fresh
     */
    fun isStale(lastFetchedAt: Long?, ttl: Duration): Boolean {
        // No timestamp = never cached, treat as stale
        if (lastFetchedAt == null) return true

        val now = clock.now().toEpochMilliseconds()
        val age = now - lastFetchedAt

        // Data is stale if older than TTL
        return age > ttl.inWholeMilliseconds
    }

    /**
     * Checks if cached data is fresh (opposite of stale).
     *
     * @param lastFetchedAt Timestamp in milliseconds when data was last fetched
     * @param ttl Time-to-live duration for this data type
     * @return true if data is fresh and can be used, false if should be refreshed
     */
    fun isFresh(lastFetchedAt: Long?, ttl: Duration): Boolean {
        return !isStale(lastFetchedAt, ttl)
    }
}
