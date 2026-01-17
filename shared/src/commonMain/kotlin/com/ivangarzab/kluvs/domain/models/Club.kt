package com.ivangarzab.kluvs.domain.models

import kotlinx.datetime.LocalDate

/**
 * Domain model for the Club entity.
 *
 * Relations ([members], [activeSession], [pastSessions]) are nullable to support flexible loading:
 * - When fetched from API, these are populated with full objects
 * - When fetched from cache or by ID reference, these may be null
 */
data class Club(

    val id: String,

    val name: String,

    /** Discord channel Snowflake ID that this Club is related to. **/
    val discordChannel: String? = null,

    /** Server ID that this Club belongs to. **/
    val serverId: String? = null,

    /** Date when this club was founded. **/
    val foundedDate: LocalDate? = null,

    /** List of member IDs in the shame list. **/
    val shameList: List<String> = emptyList(),

    /**
     * List of [Member]s in this club.
     * Null when not loaded; empty list when loaded but no members exist.
     */
    val members: List<Member>? = null,

    /**
     * Active [Session] for this club (if any).
     * Null can mean either: (1) not loaded, or (2) no active session exists.
     */
    val activeSession: Session? = null,

    /**
     * List of past [Session]s for this club.
     * Null when not loaded; empty list when loaded but no past sessions exist.
     */
    val pastSessions: List<Session>? = null
)
