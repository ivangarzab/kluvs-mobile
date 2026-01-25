package com.ivangarzab.kluvs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivangarzab.kluvs.database.entities.ClubMemberCrossRef
import com.ivangarzab.kluvs.database.entities.MemberEntity

/**
 * Data Access Object for Member entities and Club-Member relationships.
 */
@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE id = :memberId")
    suspend fun getMember(memberId: String): MemberEntity?

    @Query("SELECT * FROM members WHERE userId = :userId")
    suspend fun getMemberByUserId(userId: String): MemberEntity?

    @Query("""
        SELECT m.* FROM members m
        INNER JOIN club_members cm ON m.id = cm.memberId
        WHERE cm.clubId = :clubId
    """)
    suspend fun getMembersForClub(clubId: String): List<MemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubMemberCrossRef(crossRef: ClubMemberCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubMemberCrossRefs(crossRefs: List<ClubMemberCrossRef>)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("DELETE FROM club_members WHERE clubId = :clubId AND memberId = :memberId")
    suspend fun deleteClubMemberCrossRef(clubId: String, memberId: String)

    @Query("SELECT lastFetchedAt FROM members WHERE id = :memberId")
    suspend fun getLastFetchedAt(memberId: String): Long?

    @Query("DELETE FROM members")
    suspend fun deleteAll()

    @Query("DELETE FROM club_members")
    suspend fun deleteAllCrossRefs()
}
