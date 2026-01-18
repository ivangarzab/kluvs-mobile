package com.ivangarzab.kluvs.data.remote.mappers

import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.model.Book

/**
 * Maps a [BookDto] from the API to a [com.ivangarzab.kluvs.model.Book] domain model.
 */
fun BookDto.toDomain(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        edition = edition,
        year = year,
        isbn = isbn,
        pageCount = page_count
    )
}
