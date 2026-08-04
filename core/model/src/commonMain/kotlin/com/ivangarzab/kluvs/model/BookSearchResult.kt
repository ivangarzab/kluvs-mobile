package com.ivangarzab.kluvs.model

/**
 * A single page of book search results.
 *
 * @param books The books returned for this page.
 * @param total The backend's total result count for the query (not the size of [books]).
 */
data class BookSearchResult(
    val books: List<Book>,
    val total: Int
)
