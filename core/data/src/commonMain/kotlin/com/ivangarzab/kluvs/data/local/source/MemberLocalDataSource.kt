package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.bark.Bark
import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.database.entities.ClubMemberCrossRef
import com.ivangarzab.kluvs.model.Member

/**
 * Local data source for Member entities.
 * Handles CRUD operations with the local Room database.
 */
interface MemberLocalDataSource {
    suspend fun getMember(memberId: String): Member?
    suspend fun getMemberByUserId(userId: String): Member?
    suspend fun getMembersForClub(clubId: String): List<Member>
    suspend fun insertMember(member: Member)
    suspend fun insertMembers(members: List<Member>)
    suspend fun insertClubMemberRelationship(clubId: String, memberId: String)
    suspend fun deleteClubMemberRelationship(clubId: String, memberId: String)
    suspend fun deleteMember(memberId: String)
    suspend fun getLastFetchedAt(memberId: String): Long?
    suspend fun deleteAll()
}

/**
 * Implementation of [MemberLocalDataSource] using Room database.
 */
class MemberLocalDataSourceImpl(
    private val database: KluvsDatabase
) : MemberLocalDataSource {

    private val memberDao = database.memberDao()
    private val clubDao = database.clubDao()

    override suspend fun getMember(memberId: String): Member? {
        val memberEntity = memberDao.getMember(memberId) ?: return null
        val clubEntities = memberDao.getClubsForMember(memberId)
        val clubs = if (clubEntities.isNotEmpty()) clubEntities.map { it.toDomain() } else null
        return memberEntity.toDomain().copy(clubs = clubs)
    }

    override suspend fun getMemberByUserId(userId: String): Member? {
        val memberEntity = memberDao.getMemberByUserId(userId) ?: return null
        val clubEntities = memberDao.getClubsForMember(memberEntity.id)
        val clubs = if (clubEntities.isNotEmpty()) clubEntities.map { it.toDomain() } else null
        return memberEntity.toDomain().copy(clubs = clubs)
    }

    override suspend fun getMembersForClub(clubId: String): List<Member> {
        return memberDao.getMembersForClub(clubId).map { it.toDomain() }
    }

    override suspend fun insertMember(member: Member) {
        Bark.v("Inserting member ${member.id} into database")
        // Insert the member entity
        memberDao.insertMember(member.toEntity())

        // Insert club-member relationships, but DON'T cache club entities here.
        // Club entities should only be cached by ClubRepository with complete data.
        // We'll insert relationships only if the club already exists (ignore foreign key errors).
        member.clubs?.let { clubs ->
            Bark.v("Processing ${clubs.size} club relationships for member ${member.id}")
            var successCount = 0
            clubs.forEach { club ->
                try {
                    memberDao.insertClubMemberCrossRef(
                        ClubMemberCrossRef(clubId = club.id, memberId = member.id)
                    )
                    successCount++
                } catch (e: Exception) {
                    // Ignore foreign key violations - club will be cached later by ClubRepository
                    Bark.v("Skipping relationship for club ${club.id} (not cached yet)")
                }
            }
            Bark.v("Inserted $successCount/${clubs.size} relationships for member ${member.id}")
        }
    }

    override suspend fun insertMembers(members: List<Member>) {
        Bark.d("Inserting ${members.size} members into database")
        memberDao.insertMembers(members.map { it.toEntity() })
    }

    override suspend fun insertClubMemberRelationship(clubId: String, memberId: String) {
        Bark.d("Adding member $memberId to club $clubId")
        memberDao.insertClubMemberCrossRef(
            ClubMemberCrossRef(clubId = clubId, memberId = memberId)
        )
    }

    override suspend fun deleteClubMemberRelationship(clubId: String, memberId: String) {
        Bark.d("Removing member $memberId from club $clubId")
        memberDao.deleteClubMemberCrossRef(clubId, memberId)
    }

    override suspend fun deleteMember(memberId: String) {
        val entity = memberDao.getMember(memberId)
        if (entity != null) {
            Bark.d("Deleting member $memberId from database")
            memberDao.deleteMember(entity)
        }
    }

    override suspend fun getLastFetchedAt(memberId: String): Long? {
        return memberDao.getLastFetchedAt(memberId)
    }

    override suspend fun deleteAll() {
        Bark.d("Clearing all members from database")
        memberDao.deleteAll()
        memberDao.deleteAllCrossRefs()
    }
}
