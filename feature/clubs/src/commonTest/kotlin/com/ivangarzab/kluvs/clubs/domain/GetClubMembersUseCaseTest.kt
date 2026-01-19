package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.kluvs.clubs.domain.GetClubMembersUseCase
import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.model.Member
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetClubMembersUseCaseTest {

    private lateinit var clubRepository: ClubRepository
    private lateinit var useCase: GetClubMembersUseCase

    @BeforeTest
    fun setup() {
        clubRepository = mock<ClubRepository>()
        useCase = GetClubMembersUseCase(clubRepository)
    }

    @Test
    fun `returns members sorted by points descending`() = runTest {
        // Given
        val clubId = "club-123"
        val members = listOf(
            Member(id = "m1", name = "Alice", userId = null, role = null, points = 10, booksRead = 5),
            Member(id = "m2", name = "Bob", userId = null, role = null, points = 30, booksRead = 3),
            Member(id = "m3", name = "Charlie", userId = null, role = null, points = 20, booksRead = 7)
        )
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = members,
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        val result = useCase(clubId)

        // Then
        assertTrue(result.isSuccess)
        val memberList = result.getOrNull()!!
        assertEquals(3, memberList.size)

        // Should be sorted by points descending
        assertEquals("Bob", memberList[0].name)
        assertEquals(30, memberList[0].points)

        assertEquals("Charlie", memberList[1].name)
        assertEquals(20, memberList[1].points)

        assertEquals("Alice", memberList[2].name)
        assertEquals(10, memberList[2].points)

        verifySuspend { clubRepository.getClub(clubId) }
    }

    @Test
    fun `returns empty list when club has no members`() = runTest {
        // Given
        val clubId = "club-123"
        val club = Club(
            id = clubId,
            name = "Empty Club",
            serverId = null,
            discordChannel = null,
            members = null,
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        val result = useCase(clubId)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        verifySuspend { clubRepository.getClub(clubId) }
    }

    @Test
    fun `returns members with all required fields`() = runTest {
        // Given
        val clubId = "club-123"
        val members = listOf(
            Member(id = "m1", name = "Alice", userId = "u1", role = "admin", points = 100, booksRead = 10)
        )
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = members,
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        val result = useCase(clubId)

        // Then
        assertTrue(result.isSuccess)
        val memberList = result.getOrNull()!!
        assertEquals(1, memberList.size)
        assertEquals("m1", memberList[0].memberId)
        assertEquals("Alice", memberList[0].name)
        assertEquals(100, memberList[0].points)
        assertEquals(null, memberList[0].avatarUrl)
        verifySuspend { clubRepository.getClub(clubId) }
    }

    @Test
    fun `handles members with equal points`() = runTest {
        // Given
        val clubId = "club-123"
        val members = listOf(
            Member(id = "m1", name = "Alice", userId = null, role = null, points = 10, booksRead = 5),
            Member(id = "m2", name = "Bob", userId = null, role = null, points = 10, booksRead = 3),
            Member(id = "m3", name = "Charlie", userId = null, role = null, points = 10, booksRead = 7)
        )
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = members,
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        val result = useCase(clubId)

        // Then
        assertTrue(result.isSuccess)
        val memberList = result.getOrNull()!!
        assertEquals(3, memberList.size)
        // All should have same points
        assertEquals(10, memberList[0].points)
        assertEquals(10, memberList[1].points)
        assertEquals(10, memberList[2].points)
        verifySuspend { clubRepository.getClub(clubId) }
    }

    @Test
    fun `returns failure when repository fails`() = runTest {
        // Given
        val clubId = "club-123"
        val exception = Exception("Club not found")
        everySuspend { clubRepository.getClub(clubId) } returns Result.failure(exception)

        // When
        val result = useCase(clubId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { clubRepository.getClub(clubId) }
    }
}
