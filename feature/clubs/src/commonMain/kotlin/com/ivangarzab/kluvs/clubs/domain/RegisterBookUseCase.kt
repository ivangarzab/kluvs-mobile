package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.BookRepository
import com.ivangarzab.kluvs.model.Book

/**
 * UseCase for registering a book with the backend, obtaining a server-assigned ID.
 *
 * Used when a book selected from search results needs a real, persisted [Book.id]
 * (e.g. as a `book_id` when creating a session) — the backend creates the book if
 * it doesn't already exist, or returns the existing record otherwise.
 *
 * @param bookRepository Repository for book data
 */
class RegisterBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(book: Book): Result<Book> {
        Bark.d("Registering book via use case (title: \"${book.title}\")")
        return bookRepository.registerBook(book)
    }
}
