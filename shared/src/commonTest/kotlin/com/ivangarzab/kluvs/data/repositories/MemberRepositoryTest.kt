package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.remote.source.MemberRemoteDataSource
import com.ivangarzab.kluvs.domain.models.Member
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemberRepositoryTest {

    private lateinit var remoteDataSource: MemberRemoteDataSource
    private lateinit var repository: MemberRepository

    @BeforeTest
    fun setup() {
        remoteDataSource = mock<MemberRemoteDataSource>()
        repository = MemberRepositoryImpl(remoteDataSource)
    }

    // ========================================
    // GET MEMBER
    // ========================================

    @Test
    fun `getMember success returns Member with details`() = runTest {
        val memberId = "member-123"
        val expectedMember = Member(
            id = memberId,
            name = "John Doe",
            points = 100,
            booksRead = 5,
            userId = "user-789",
            role = "Reader"
        )
        everySuspend { remoteDataSource.getMember(memberId) } returns Result.success(expectedMember)

        val result = repository.getMember(memberId)

        assertTrue(result.isSuccess)
        assertEquals(expectedMember, result.getOrNull())
        assertEquals("John Doe", result.getOrNull()?.name)
        assertEquals(100, result.getOrNull()?.points)
        assertEquals(5, result.getOrNull()?.booksRead)
        verifySuspend { remoteDataSource.getMember(memberId) }
    }

    @Test
    fun `getMember failure returns Result failure`() = runTest {
        val memberId = "member-123"
        val exception = Exception("Member not found")
        everySuspend { remoteDataSource.getMember(memberId) } returns Result.failure(exception)

        val result = repository.getMember(memberId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.getMember(memberId) }
    }

    @Test
    fun `getMember with non-existent member returns failure`() = runTest {
        val memberId = "non-existent"
        val exception = Exception("Member not found")
        everySuspend { remoteDataSource.getMember(memberId) } returns Result.failure(exception)

        val result = repository.getMember(memberId)

        assertTrue(result.isFailure)
        verifySuspend { remoteDataSource.getMember(memberId) }
    }

    // ========================================
    // GET MEMBER BY USER ID
    // ========================================

    @Test
    fun `getMemberByUserId success returns Member`() = runTest {
        val userId = "user-789"
        val expectedMember = Member(
            id = "member-123",
            name = "John",
            points = 50,
            booksRead = 3,
            userId = userId
        )
        everySuspend { remoteDataSource.getMemberByUserId(userId) } returns Result.success(expectedMember)

        val result = repository.getMemberByUserId(userId)

        assertTrue(result.isSuccess)
        assertEquals(expectedMember, result.getOrNull())
        assertEquals(userId, result.getOrNull()?.userId)
        verifySuspend { remoteDataSource.getMemberByUserId(userId) }
    }

    @Test
    fun `getMemberByUserId failure returns Result failure`() = runTest {
        val userId = "user-789"
        val exception = Exception("Member not found for user")
        everySuspend { remoteDataSource.getMemberByUserId(userId) } returns Result.failure(exception)

        val result = repository.getMemberByUserId(userId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.getMemberByUserId(userId) }
    }

    // ========================================
    // CREATE MEMBER
    // ========================================

    @Test
    fun `createMember success creates member with all fields`() = runTest {
        val memberName = "Jane Doe"
        val userId = "user-456"
        val role = "Admin"
        val clubIds = listOf("club-1", "club-2")
        val expectedMember = Member(
            id = "member-new",
            name = memberName,
            userId = userId,
            role = role,
            points = 0,
            booksRead = 0
        )
        everySuspend { remoteDataSource.createMember(any()) } returns Result.success(expectedMember)

        val result = repository.createMember(memberName, userId, role, clubIds)

        assertTrue(result.isSuccess)
        assertEquals(expectedMember, result.getOrNull())
        assertEquals(memberName, result.getOrNull()?.name)
        verifySuspend { remoteDataSource.createMember(any()) }
    }

    @Test
    fun `createMember success creates member without optional fields`() = runTest {
        val memberName = "Simple Member"
        val expectedMember = Member(
            id = "member-new",
            name = memberName,
            points = 0,
            booksRead = 0
        )
        everySuspend { remoteDataSource.createMember(any()) } returns Result.success(expectedMember)

        val result = repository.createMember(memberName, null, null, null)

        assertTrue(result.isSuccess)
        assertEquals(expectedMember, result.getOrNull())
        assertEquals(memberName, result.getOrNull()?.name)
        verifySuspend { remoteDataSource.createMember(any()) }
    }

    @Test
    fun `createMember using default clubIds parameter`() = runTest {
        val memberName = "Member Without Clubs"
        val expectedMember = Member(
            id = "member-no-clubs",
            name = memberName,
            points = 0,
            booksRead = 0
        )
        everySuspend { remoteDataSource.createMember(any()) } returns Result.success(expectedMember)

        val result = repository.createMember(memberName, "user-123", "Reader")

        assertTrue(result.isSuccess)
        assertEquals(expectedMember, result.getOrNull())
        verifySuspend { remoteDataSource.createMember(any()) }
    }

    @Test
    fun `createMember with club IDs adds member to clubs`() = runTest {
        val memberName = "New Member"
        val clubIds = listOf("club-1", "club-2", "club-3")
        val expectedMember = Member(
            id = "member-new",
            name = memberName,
            points = 0,
            booksRead = 0
        )
        everySuspend { remoteDataSource.createMember(any()) } returns Result.success(expectedMember)

        val result = repository.createMember(memberName, null, null, clubIds)

        assertTrue(result.isSuccess)
        verifySuspend { remoteDataSource.createMember(any()) }
    }

    @Test
    fun `createMember failure returns Result failure`() = runTest {
        val exception = Exception("Failed to create member")
        everySuspend { remoteDataSource.createMember(any()) } returns Result.failure(exception)

        val result = repository.createMember("Jane", null, null, null)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.createMember(any()) }
    }

    // ========================================
    // UPDATE MEMBER
    // ========================================

    @Test
    fun `updateMember with all fields updates all values`() = runTest {
        val memberId = "member-123"
        val newName = "Updated Name"
        val newUserId = "user-new"
        val newRole = "Moderator"
        val newPoints = 200
        val newBooksRead = 15
        val expectedMember = Member(
            id = memberId,
            name = newName,
            userId = newUserId,
            role = newRole,
            points = newPoints,
            booksRead = newBooksRead
        )
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.success(expectedMember)

        val result = repository.updateMember(
            memberId = memberId,
            name = newName,
            userId = newUserId,
            role = newRole,
            points = newPoints,
            booksRead = newBooksRead
        )

        assertTrue(result.isSuccess)
        assertEquals(newName, result.getOrNull()?.name)
        assertEquals(newPoints, result.getOrNull()?.points)
        assertEquals(newBooksRead, result.getOrNull()?.booksRead)
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    @Test
    fun `updateMember with null name does not update name`() = runTest {
        val memberId = "member-123"
        val expectedMember = Member(
            id = memberId,
            name = "Unchanged",
            points = 150,
            booksRead = 10
        )
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.success(expectedMember)

        val result = repository.updateMember(memberId = memberId, name = null, points = 150)

        assertTrue(result.isSuccess)
        assertEquals("Unchanged", result.getOrNull()?.name)
        assertEquals(150, result.getOrNull()?.points)
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    @Test
    fun `updateMember with null points does not update points`() = runTest {
        val memberId = "member-123"
        val expectedMember = Member(
            id = memberId,
            name = "Updated Name",
            points = 100,
            booksRead = 5
        )
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.success(expectedMember)

        val result = repository.updateMember(memberId = memberId, name = "Updated Name", points = null)

        assertTrue(result.isSuccess)
        assertEquals("Updated Name", result.getOrNull()?.name)
        assertEquals(100, result.getOrNull()?.points)
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    @Test
    fun `updateMember with clubIds replaces all club memberships`() = runTest {
        val memberId = "member-123"
        val newClubIds = listOf("club-5", "club-6")
        val expectedMember = Member(
            id = memberId,
            name = "Member",
            points = 100,
            booksRead = 5
        )
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.success(expectedMember)

        val result = repository.updateMember(memberId = memberId, clubIds = newClubIds)

        assertTrue(result.isSuccess)
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    @Test
    fun `updateMember with empty clubIds removes member from all clubs`() = runTest {
        val memberId = "member-123"
        val emptyClubIds = emptyList<String>()
        val expectedMember = Member(
            id = memberId,
            name = "Member",
            points = 100,
            booksRead = 5
        )
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.success(expectedMember)

        val result = repository.updateMember(memberId = memberId, clubIds = emptyClubIds)

        assertTrue(result.isSuccess)
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    @Test
    fun `updateMember with null clubIds does not change club memberships`() = runTest {
        val memberId = "member-123"
        val expectedMember = Member(
            id = memberId,
            name = "Updated Name",
            points = 100,
            booksRead = 5
        )
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.success(expectedMember)

        val result = repository.updateMember(memberId = memberId, name = "Updated Name", clubIds = null)

        assertTrue(result.isSuccess)
        assertEquals("Updated Name", result.getOrNull()?.name)
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    @Test
    fun `updateMember failure returns Result failure`() = runTest {
        val exception = Exception("Failed to update member")
        everySuspend { remoteDataSource.updateMember(any()) } returns Result.failure(exception)

        val result = repository.updateMember("member-123", name = "Updated")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.updateMember(any()) }
    }

    // ========================================
    // DELETE MEMBER
    // ========================================

    @Test
    fun `deleteMember success returns success message`() = runTest {
        val memberId = "member-123"
        val successMessage = "Member deleted successfully"
        everySuspend { remoteDataSource.deleteMember(memberId) } returns Result.success(successMessage)

        val result = repository.deleteMember(memberId)

        assertTrue(result.isSuccess)
        assertEquals(successMessage, result.getOrNull())
        verifySuspend { remoteDataSource.deleteMember(memberId) }
    }

    @Test
    fun `deleteMember failure returns Result failure`() = runTest {
        val memberId = "member-123"
        val exception = Exception("Failed to delete member")
        everySuspend { remoteDataSource.deleteMember(memberId) } returns Result.failure(exception)

        val result = repository.deleteMember(memberId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.deleteMember(memberId) }
    }

    @Test
    fun `deleteMember with non-existent member returns failure`() = runTest {
        val memberId = "non-existent"
        val exception = Exception("Member not found")
        everySuspend { remoteDataSource.deleteMember(memberId) } returns Result.failure(exception)

        val result = repository.deleteMember(memberId)

        assertTrue(result.isFailure)
        verifySuspend { remoteDataSource.deleteMember(memberId) }
    }
}
