package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.data.remote.dtos.CreateSessionRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateSessionRequestDto
import com.ivangarzab.kluvs.data.remote.mappers.toDto
import com.ivangarzab.kluvs.data.remote.source.SessionRemoteDataSource
import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.model.Discussion
import com.ivangarzab.kluvs.model.Session
import kotlinx.datetime.LocalDateTime

/**
 * Repository for managing Session data.
 *
 * This repository abstracts the data source layer and provides a clean API for accessing
 * session-related data. Currently delegates to remote data source, but can be extended
 * to support local caching and offline capabilities.
 */
interface SessionRepository {

    /**
     * Retrieves a single session by its ID.
     *
     * @param sessionId The ID of the session to retrieve
     * @return Result containing the Session (with nested book, discussions, shame list, etc.)
     *         if successful, or an error if the operation failed
     */
    suspend fun getSession(sessionId: String): Result<Session>

    /**
     * Creates a new reading session.
     *
     * @param clubId The ID of the club this session belongs to
     * @param book The book for this reading session
     * @param dueDate Optional due date for completing the book
     * @param discussions Optional list of discussions to create with this session
     * @return Result containing the created Session if successful, or an error if the operation failed
     */
    suspend fun createSession(
        clubId: String,
        book: Book,
        dueDate: LocalDateTime?,
        discussions: List<Discussion>? = null
    ): Result<Session>

    /**
     * Updates an existing session.
     *
     * Uses PATCH semantics - only fields that are non-null will be updated.
     * Pass null for any field you want to leave unchanged.
     *
     * @param sessionId The ID of the session to update
     * @param book Optional new Book to replace the session's book (null = don't update book)
     * @param dueDate Optional new due date (null = don't update due date)
     * @param discussions Optional list of discussions to replace all discussions (null = don't update discussions)
     * @param discussionIdsToDelete Optional list of discussion IDs to delete
     * @return Result containing the updated Session if successful, or an error if the operation failed
     *
     * Note: When providing discussions, it will replace ALL discussions. To add/remove individual
     * discussions, use the dedicated methods or provide the complete list with additions/removals.
     */
    suspend fun updateSession(
        sessionId: String,
        book: Book? = null,
        dueDate: LocalDateTime? = null,
        discussions: List<Discussion>? = null,
        discussionIdsToDelete: List<String>? = null
    ): Result<Session>

    /**
     * Deletes a session by its ID.
     *
     * @param sessionId The ID of the session to delete
     * @return Result containing success message if deletion was successful, or an error if the operation failed
     */
    suspend fun deleteSession(sessionId: String): Result<String>
}

/**
 * Implementation of [SessionRepository] that delegates to remote data sources.
 *
 * This is a simple pass-through implementation that can be extended later to include
 * caching strategies, offline support, and data synchronization.
 *
 * Note: The API returns nested data (book, discussions, shame list) with Session responses.
 * Future implementations may decompose this nested data and coordinate with other
 * repositories for caching purposes.
 */
internal class SessionRepositoryImpl(
    private val sessionRemoteDataSource: SessionRemoteDataSource
) : SessionRepository {

    override suspend fun getSession(sessionId: String): Result<Session> =
        sessionRemoteDataSource.getSession(sessionId)

    override suspend fun createSession(
        clubId: String,
        book: Book,
        dueDate: LocalDateTime?,
        discussions: List<Discussion>?
    ): Result<Session> =
        sessionRemoteDataSource.createSession(
            CreateSessionRequestDto(
                club_id = clubId,
                book = BookDto(
                    title = book.title,
                    author = book.author,
                    edition = book.edition,
                    year = book.year,
                    isbn = book.isbn
                ),
                due_date = dueDate?.toString(),
                discussions = discussions?.map { it.toDto() }
            )
        )

    override suspend fun updateSession(
        sessionId: String,
        book: Book?,
        dueDate: LocalDateTime?,
        discussions: List<Discussion>?,
        discussionIdsToDelete: List<String>?
    ): Result<Session> =
        sessionRemoteDataSource.updateSession(
            UpdateSessionRequestDto(
                id = sessionId,
                book = book?.let {
                    BookDto(
                        title = it.title,
                        author = it.author,
                        edition = it.edition,
                        year = it.year,
                        isbn = it.isbn
                    )
                },
                due_date = dueDate?.toString(),
                discussions = discussions?.map { it.toDto() },
                discussion_ids_to_delete = discussionIdsToDelete
            )
        )

    override suspend fun deleteSession(sessionId: String): Result<String> =
        sessionRemoteDataSource.deleteSession(sessionId)
}