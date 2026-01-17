package com.ivangarzab.kluvs.domain.models

/**
 * Domain model for the Book entity.
 */
data class Book(

    val id: String? = null,

    val title: String,

    val author: String,

    val edition: String? = null,

    val year: Int? = null,

    val isbn: String?,

    val pageCount: Int? = null
)
