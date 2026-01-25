package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.model.Session
import com.ivangarzab.bark.Bark

/**
 * Local data source for Session entities.
 * Handles CRUD operations with the local Room database.
 */
interface SessionLocalDataSource {
    suspend fun getSession(sessionId: String): Session?
    suspend fun getSessionsForClub(clubId: String): List<Session>
    suspend fun insertSession(session: Session)
    suspend fun insertSessions(sessions: List<Session>)
    suspend fun deleteSession(sessionId: String)
    suspend fun getLastFetchedAt(sessionId: String): Long?
    suspend fun deleteAll()
}

/**
 * Implementation of [SessionLocalDataSource] using Room database.
 */
class SessionLocalDataSourceImpl(
    private val database: KluvsDatabase
) : SessionLocalDataSource {

    private val sessionDao = database.sessionDao()
    private val bookDao = database.bookDao()

    override suspend fun getSession(sessionId: String): Session? {
        val sessionEntity = sessionDao.getSession(sessionId) ?: return null
        val bookId = sessionEntity.bookId ?: return null
        val bookEntity = bookDao.getBook(bookId) ?: return null
        return sessionEntity.toDomain(bookEntity.toDomain())
    }

    override suspend fun getSessionsForClub(clubId: String): List<Session> {
        return sessionDao.getSessionsForClub(clubId).mapNotNull { sessionEntity ->
            val bookId = sessionEntity.bookId ?: return@mapNotNull null
            val bookEntity = bookDao.getBook(bookId) ?: return@mapNotNull null
            sessionEntity.toDomain(bookEntity.toDomain())
        }
    }

    override suspend fun insertSession(session: Session) {
        Bark.d("Inserting session ${session.id} into database")
        // First insert the book if it has an ID
        session.book.id?.let { bookId ->
            bookDao.insertBook(session.book.toEntity())
        }
        // Then insert the session
        sessionDao.insertSession(session.toEntity())
    }

    override suspend fun insertSessions(sessions: List<Session>) {
        Bark.d("Inserting ${sessions.size} sessions into database")
        // Insert all books first
        val books = sessions.mapNotNull { it.book.takeIf { book -> book.id != null } }
        bookDao.insertBooks(books.map { it.toEntity() })

        // Then insert all sessions
        sessionDao.insertSessions(sessions.map { it.toEntity() })
    }

    override suspend fun deleteSession(sessionId: String) {
        val entity = sessionDao.getSession(sessionId)
        if (entity != null) {
            Bark.d("Deleting session $sessionId from database")
            sessionDao.deleteSession(entity)
        }
    }

    override suspend fun getLastFetchedAt(sessionId: String): Long? {
        return sessionDao.getLastFetchedAt(sessionId)
    }

    override suspend fun deleteAll() {
        Bark.d("Clearing all sessions from database")
        sessionDao.deleteAll()
    }
}
