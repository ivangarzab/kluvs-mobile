package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.database.entities.ClubMemberCrossRef
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.bark.Bark

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

    override suspend fun getMember(memberId: String): Member? {
        return memberDao.getMember(memberId)?.toDomain()
    }

    override suspend fun getMemberByUserId(userId: String): Member? {
        return memberDao.getMemberByUserId(userId)?.toDomain()
    }

    override suspend fun getMembersForClub(clubId: String): List<Member> {
        return memberDao.getMembersForClub(clubId).map { it.toDomain() }
    }

    override suspend fun insertMember(member: Member) {
        Bark.d("Inserting member ${member.id} into database")
        memberDao.insertMember(member.toEntity())
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
