package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.bark.Bark

/**
 * Local data source for Club entities.
 * Handles CRUD operations with the local Room database.
 */
interface ClubLocalDataSource {
    suspend fun getClub(clubId: String): Club?
    suspend fun getClubsForServer(serverId: String): List<Club>
    suspend fun insertClub(club: Club)
    suspend fun insertClubs(clubs: List<Club>)
    suspend fun deleteClub(clubId: String)
    suspend fun getLastFetchedAt(clubId: String): Long?
    suspend fun deleteAll()
}

/**
 * Implementation of [ClubLocalDataSource] using Room database.
 */
class ClubLocalDataSourceImpl(
    private val database: KluvsDatabase
) : ClubLocalDataSource {

    private val clubDao = database.clubDao()
    private val memberDao = database.memberDao()
    private val sessionDao = database.sessionDao()
    private val bookDao = database.bookDao()
    private val discussionDao = database.discussionDao()

    override suspend fun getClub(clubId: String): Club? {
        val clubEntity = clubDao.getClub(clubId) ?: return null

        return try {
            // Load members for this club
            val memberEntities = memberDao.getMembersForClub(clubId)
            val members = if (memberEntities.isNotEmpty()) memberEntities.map { it.toDomain() } else null

            // Load active session for this club (get the most recent session)
            val sessionEntities = sessionDao.getSessionsForClub(clubId)
            val activeSession = sessionEntities.firstOrNull()?.let { sessionEntity ->
                val bookId = sessionEntity.bookId ?: return@let null
                val bookEntity = bookDao.getBook(bookId) ?: return@let null
                val discussions = discussionDao.getDiscussionsForSession(sessionEntity.id).map { it.toDomain() }
                sessionEntity.toDomain(bookEntity.toDomain()).copy(discussions = discussions)
            }

            clubEntity.toDomain().copy(
                members = members,
                activeSession = activeSession
            )
        } catch (e: Exception) {
            Bark.e("Failed to load club ${clubId} from cache with relationships", e)
            // Return just the basic club without relationships if loading fails
            clubEntity.toDomain()
        }
    }

    override suspend fun getClubsForServer(serverId: String): List<Club> {
        return clubDao.getClubsForServer(serverId).map { it.toDomain() }
    }

    override suspend fun insertClub(club: Club) {
        Bark.v("Inserting club ${club.id} into database")

        try {
            // Insert club entity
            clubDao.insertClub(club.toEntity())

            // Cache members and relationships
            // Note: Members from Club API response are basic objects without their own relationships.
            // We cache them here so the club can show its member list, but MemberRepository
            // is responsible for caching complete member data.
            club.members?.let { members ->
                Bark.v("Caching ${members.size} members for club ${club.id}")
                memberDao.insertMembers(members.map { it.toEntity() })
                members.forEach { member ->
                    memberDao.insertClubMemberCrossRef(
                        com.ivangarzab.kluvs.database.entities.ClubMemberCrossRef(
                            clubId = club.id,
                            memberId = member.id
                        )
                    )
                }
            }

            // Cache active session with book and discussions
            club.activeSession?.let { session ->
                Bark.v("Caching active session ${session.id} for club ${club.id}")

                // Cache the book
                try {
                    bookDao.insertBook(session.book.toEntity())
                } catch (e: Exception) {
                    Bark.e("Failed to cache book for session ${session.id}: ${e.message}", e)
                }

                // Cache the session
                try {
                    sessionDao.insertSession(session.toEntity())
                } catch (e: Exception) {
                    Bark.e("Failed to cache session ${session.id}: ${e.message}", e)
                }

                // Cache discussions
                session.discussions?.forEach { discussion ->
                    try {
                        discussionDao.insertDiscussion(discussion.toEntity())
                    } catch (e: Exception) {
                        Bark.e("Failed to cache discussion ${discussion.id}: ${e.message}", e)
                    }
                }
            }
        } catch (e: Exception) {
            Bark.e("Failed to insert club ${club.id} into database", e)
            throw e
        }
    }

    override suspend fun insertClubs(clubs: List<Club>) {
        Bark.v("Inserting ${clubs.size} clubs into database")
        clubDao.insertClubs(clubs.map { it.toEntity() })
    }

    override suspend fun deleteClub(clubId: String) {
        val entity = clubDao.getClub(clubId)
        if (entity != null) {
            Bark.v("Deleting club $clubId from database")
            clubDao.deleteClub(entity)
        }
    }

    override suspend fun getLastFetchedAt(clubId: String): Long? {
        return clubDao.getLastFetchedAt(clubId)
    }

    override suspend fun deleteAll() {
        Bark.v("Clearing all clubs from database")
        clubDao.deleteAll()
    }
}
