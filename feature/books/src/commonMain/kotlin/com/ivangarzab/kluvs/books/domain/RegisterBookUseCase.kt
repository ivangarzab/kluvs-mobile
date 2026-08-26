package com.ivangarzab.kluvs.books.domain

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.repositories.BookRepository
import com.ivangarzab.kluvs.model.Book

/**
 * UseCase for registering a book with the backend, assigning it a server-side numeric ID.
 *
 * Books arriving fresh from search (or a "more by this author" row) only carry a Google
 * Books volume ID until registered, and can't be shelved or liked until then.
 *
 * @param bookRepository Repository for book data
 */
class RegisterBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(book: Book): Result<Book> {
        Bark.d("Registering book via use case (title: ${book.title})")
        return bookRepository.registerBook(book)
    }
}
