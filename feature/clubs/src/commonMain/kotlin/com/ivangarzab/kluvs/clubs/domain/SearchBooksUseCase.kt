package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.BookRepository
import com.ivangarzab.kluvs.model.Book

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
    /**
     * Searches for books matching the given query.
     *
     * @param query Free-text search query
     * @return Result containing a list of matching [Book]s, or an error
     */
    suspend operator fun invoke(query: String): Result<List<Book>> {
        Bark.d("Searching books via use case (query: \"$query\")")
        return bookRepository.searchBooks(query)
    }
}
