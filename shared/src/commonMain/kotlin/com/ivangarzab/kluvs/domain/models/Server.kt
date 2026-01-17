package com.ivangarzab.kluvs.domain.models

/**
 * Domain model for the (Discord) Server entity.
 *
 * Relations ([clubs]) are nullable to support flexible loading:
 * - When fetched from API with expand, clubs are populated
 * - When fetched as basic server info, clubs may be null
 */
data class Server(

    val id: String,

    val name: String,

    /**
     * List of [Club]s in this server.
     * Null when not loaded; empty list when loaded but no clubs exist.
     */
    val clubs: List<Club>? = null
)