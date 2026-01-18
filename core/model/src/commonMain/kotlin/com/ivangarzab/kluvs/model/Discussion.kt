package com.ivangarzab.kluvs.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model for the Discussion entity.
 */
data class Discussion(

    val id: String,

    /** Session ID that this Discussion belongs to. **/
    val sessionId: String? = null,

    val title: String,

    val date: LocalDateTime,

    val location: String? = null
)