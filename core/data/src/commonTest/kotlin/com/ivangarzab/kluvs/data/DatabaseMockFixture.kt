package com.ivangarzab.kluvs.data

import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.database.dao.BookDao
import com.ivangarzab.kluvs.database.dao.ClubDao
import com.ivangarzab.kluvs.database.dao.DiscussionDao
import com.ivangarzab.kluvs.database.dao.MemberDao
import com.ivangarzab.kluvs.database.dao.ServerDao
import com.ivangarzab.kluvs.database.dao.SessionDao
import com.ivangarzab.kluvs.database.entities.BookEntity
import com.ivangarzab.kluvs.database.entities.ClubEntity
import com.ivangarzab.kluvs.database.entities.ClubMemberCrossRef
import com.ivangarzab.kluvs.database.entities.DiscussionEntity
import com.ivangarzab.kluvs.database.entities.MemberEntity
import com.ivangarzab.kluvs.database.entities.ServerEntity
import com.ivangarzab.kluvs.database.entities.SessionEntity
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock

/**
 * Test fixture for mocking a KluvsDatabase instance with all DAOs.
 *
 * Provides pre-configured mock DAOs that satisfy the database interface.
 * All DAO methods are mocked with default behaviors (returning Unit or empty lists).
 *
 * Usage:
 * ```
 * val fixture = DatabaseMockFixture()
 * val dataSource = ClubLocalDataSourceImpl(fixture.database)
 * // Use fixture.clubDao, fixture.memberDao, etc. to override specific behaviors
 * ```
 */
class DatabaseMockFixture {
    val clubDao: ClubDao = mock<ClubDao>()
    val memberDao: MemberDao = mock<MemberDao>()
    val sessionDao: SessionDao = mock<SessionDao>()
    val bookDao: BookDao = mock<BookDao>()
    val discussionDao: DiscussionDao = mock<DiscussionDao>()
    val serverDao: ServerDao = mock<ServerDao>()

    val database: KluvsDatabase = mock<KluvsDatabase>().also { db ->
        every { db.clubDao() } returns clubDao
        every { db.memberDao() } returns memberDao
        every { db.sessionDao() } returns sessionDao
        every { db.bookDao() } returns bookDao
        every { db.discussionDao() } returns discussionDao
        every { db.serverDao() } returns serverDao

        // Mock all insert/update operations to accept any argument and return Unit
        everySuspend { clubDao.insertClub(any()) } returns Unit
        everySuspend { clubDao.insertClubs(any()) } returns Unit
        everySuspend { clubDao.deleteClub(any()) } returns Unit
        everySuspend { clubDao.deleteAll() } returns Unit

        everySuspend { memberDao.insertMember(any()) } returns Unit
        everySuspend { memberDao.insertMembers(any()) } returns Unit
        everySuspend { memberDao.insertClubMemberCrossRef(any()) } returns Unit
        everySuspend { memberDao.deleteMember(any()) } returns Unit
        everySuspend { memberDao.deleteClubMemberCrossRef(any(), any()) } returns Unit
        everySuspend { memberDao.deleteAll() } returns Unit
        everySuspend { memberDao.deleteAllCrossRefs() } returns Unit

        everySuspend { sessionDao.insertSession(any()) } returns Unit
        everySuspend { sessionDao.insertSessions(any()) } returns Unit
        everySuspend { sessionDao.deleteSession(any()) } returns Unit
        everySuspend { sessionDao.deleteAll() } returns Unit

        everySuspend { bookDao.insertBook(any()) } returns Unit
        everySuspend { bookDao.insertBooks(any()) } returns Unit
        everySuspend { bookDao.deleteBook(any()) } returns Unit
        everySuspend { bookDao.deleteAll() } returns Unit

        everySuspend { discussionDao.insertDiscussion(any()) } returns Unit
        everySuspend { discussionDao.deleteAll() } returns Unit

        everySuspend { serverDao.insertServer(any()) } returns Unit
        everySuspend { serverDao.deleteServer(any()) } returns Unit
        everySuspend { serverDao.deleteAll() } returns Unit

        // Mock read operations to return empty lists by default
        everySuspend { clubDao.getClub(any()) } returns null
        everySuspend { clubDao.getClubsForServer(any()) } returns emptyList()
        everySuspend { memberDao.getMember(any()) } returns null
        everySuspend { memberDao.getMemberByUserId(any()) } returns null
        everySuspend { memberDao.getMembersForClub(any()) } returns emptyList()
        everySuspend { memberDao.getClubsForMember(any()) } returns emptyList()
        everySuspend { sessionDao.getSession(any()) } returns null
        everySuspend { sessionDao.getSessionsForClub(any()) } returns emptyList()
        everySuspend { bookDao.getBook(any()) } returns null
        everySuspend { bookDao.getAllBooks() } returns emptyList()
        everySuspend { serverDao.getServer(any()) } returns null
        everySuspend { serverDao.getAllServers() } returns emptyList()
        everySuspend { discussionDao.getDiscussionsForSession(any()) } returns emptyList()
    }
}