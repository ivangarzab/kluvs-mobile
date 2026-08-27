package com.ivangarzab.kluvs.books.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.BookRepository
import com.ivangarzab.kluvs.model.BookSearchResult

/**
 * UseCase for searching books by a free-text query.
 *
 * Results come from the Google Books API via the backend and are not cached.
 *
 * @param bookRepository Repository for book data
 */
class SearchBooksUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(query: String, offset: Int = 0): Result<BookSearchResult> {
        Bark.d("Searching books via use case (query: \"$query\", offset: $offset)")
        return bookRepository.searchBooks(query, offset)
    }
}
