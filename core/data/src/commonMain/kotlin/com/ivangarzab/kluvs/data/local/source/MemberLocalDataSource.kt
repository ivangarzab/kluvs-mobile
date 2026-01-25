package com.ivangarzab.kluvs.data.local.source

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
        memberDao.insertMember(member.toEntity())
    }

    override suspend fun insertMembers(members: List<Member>) {
        memberDao.insertMembers(members.map { it.toEntity() })
    }

    override suspend fun insertClubMemberRelationship(clubId: String, memberId: String) {
        memberDao.insertClubMemberCrossRef(
            ClubMemberCrossRef(clubId = clubId, memberId = memberId)
        )
    }

    override suspend fun deleteClubMemberRelationship(clubId: String, memberId: String) {
        memberDao.deleteClubMemberCrossRef(clubId, memberId)
    }

    override suspend fun deleteMember(memberId: String) {
        val entity = memberDao.getMember(memberId)
        if (entity != null) {
            memberDao.deleteMember(entity)
        }
    }

    override suspend fun getLastFetchedAt(memberId: String): Long? {
        return memberDao.getLastFetchedAt(memberId)
    }

    override suspend fun deleteAll() {
        memberDao.deleteAll()
        memberDao.deleteAllCrossRefs()
    }
}
