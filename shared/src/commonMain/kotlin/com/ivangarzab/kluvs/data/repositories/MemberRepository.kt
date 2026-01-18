package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.remote.dtos.CreateMemberRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateMemberRequestDto
import com.ivangarzab.kluvs.data.remote.source.MemberRemoteDataSource
import com.ivangarzab.kluvs.model.Member

/**
 * Repository for managing Member data.
 *
 * This repository abstracts the data source layer and provides a clean API for accessing
 * member-related data. Currently delegates to remote data source, but can be extended
 * to support local caching and offline capabilities.
 */
interface MemberRepository {

    /**
     * Retrieves a single member by their ID.
     *
     * @param memberId The ID of the member to retrieve
     * @return Result containing the Member (with nested clubs, shame clubs, etc.) if successful,
     *         or an error if the operation failed
     */
    suspend fun getMember(memberId: String): Result<Member>

    /**
     * Retrieves a member by their Discord user ID.
     *
     * @param userId The Discord user ID
     * @return Result containing the Member if successful, or an error if the operation failed
     */
    suspend fun getMemberByUserId(userId: String): Result<Member>

    /**
     * Creates a new member.
     *
     * @param name The name of the member
     * @param userId Optional Discord user ID to associate with this member
     * @param role Optional role for the member
     * @param clubIds Optional list of club IDs to add this member to
     * @return Result containing the created Member if successful, or an error if the operation failed
     */
    suspend fun createMember(
        name: String,
        userId: String?,
        role: String?,
        clubIds: List<String>? = null
    ): Result<Member>

    /**
     * Updates an existing member.
     *
     * Uses PATCH semantics - only fields that are non-null will be updated.
     * Pass null for any field you want to leave unchanged.
     *
     * @param memberId The ID of the member to update
     * @param name Optional new name for the member (null to keep current value)
     * @param userId Optional new Discord user ID (null to keep current value)
     * @param role Optional new role (null to keep current value)
     * @param points Optional new points value (null to keep current value)
     * @param booksRead Optional new books read count (null to keep current value)
     * @param clubIds Optional list of club IDs to set as the member's clubs (null to keep current clubs).
     *                When provided, this REPLACES all club memberships with the new list.
     * @return Result containing the updated Member if successful, or an error if the operation failed
     */
    suspend fun updateMember(
        memberId: String,
        name: String? = null,
        userId: String? = null,
        role: String? = null,
        points: Int? = null,
        booksRead: Int? = null,
        clubIds: List<String>? = null
    ): Result<Member>

    /**
     * Deletes a member by their ID.
     *
     * @param memberId The ID of the member to delete
     * @return Result containing success message if deletion was successful, or an error if the operation failed
     */
    suspend fun deleteMember(memberId: String): Result<String>
}

/**
 * Implementation of [MemberRepository] that delegates to remote data sources.
 *
 * This is a simple pass-through implementation that can be extended later to include
 * caching strategies, offline support, and data synchronization.
 *
 * Note: The API returns nested data (clubs, shame clubs) with Member responses.
 * Future implementations may decompose this nested data and coordinate with other
 * repositories for caching purposes.
 */
internal class MemberRepositoryImpl(
    private val memberRemoteDataSource: MemberRemoteDataSource
) : MemberRepository {

    override suspend fun getMember(memberId: String): Result<Member> =
        memberRemoteDataSource.getMember(memberId)

    override suspend fun getMemberByUserId(userId: String): Result<Member> =
        memberRemoteDataSource.getMemberByUserId(userId)

    override suspend fun createMember(
        name: String,
        userId: String?,
        role: String?,
        clubIds: List<String>?
    ): Result<Member> =
        memberRemoteDataSource.createMember(
            CreateMemberRequestDto(
                name = name,
                user_id = userId,
                role = role,
                clubs = clubIds
            )
        )

    override suspend fun updateMember(
        memberId: String,
        name: String?,
        userId: String?,
        role: String?,
        points: Int?,
        booksRead: Int?,
        clubIds: List<String>?
    ): Result<Member> =
        memberRemoteDataSource.updateMember(
            UpdateMemberRequestDto(
                id = memberId,
                name = name,
                user_id = userId,
                role = role,
                points = points,
                books_read = booksRead,
                clubs = clubIds
            )
        )

    override suspend fun deleteMember(memberId: String): Result<String> =
        memberRemoteDataSource.deleteMember(memberId)
}
