package com.ivangarzab.kluvs.clubs.domain

import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.kluvs.model.Role
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LeaveClubUseCaseTest {

    private val memberRepository = mock<MemberRepository>()
    private val useCase = LeaveClubUseCase(memberRepository)

    private val targetClub = Club(id = "club-1", name = "Test Club")
    private val otherClub = Club(id = "club-2", name = "Other Club")
    private val selfMember = Member(
        id = "m1", userId = "u1", name = "Alice", booksRead = 0,
        clubs = listOf(targetClub, otherClub)
    )
    private val updatedMember = selfMember.copy(clubs = listOf(otherClub))

    private val params = LeaveClubUseCase.Params(
        memberId = "m1",
        clubId = "club-1"
    )

    @Test
    fun `invoke succeeds when MEMBER leaves`() = runTest {
        everySuspend { memberRepository.getMember("m1", forceRefresh = true) } returns Result.success(selfMember)
        everySuspend { memberRepository.updateMember(memberId = "m1", clubIds = listOf("club-2")) } returns
            Result.success(updatedMember)

        val result = useCase(params, Role.MEMBER)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke succeeds when ADMIN leaves`() = runTest {
        everySuspend { memberRepository.getMember("m1", forceRefresh = true) } returns Result.success(selfMember)
        everySuspend { memberRepository.updateMember(memberId = "m1", clubIds = listOf("club-2")) } returns
            Result.success(updatedMember)

        val result = useCase(params, Role.ADMIN)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke strips only the target club from member clubs`() = runTest {
        everySuspend { memberRepository.getMember("m1", forceRefresh = true) } returns Result.success(selfMember)
        everySuspend { memberRepository.updateMember(memberId = "m1", clubIds = listOf("club-2")) } returns
            Result.success(updatedMember)

        val result = useCase(params, Role.MEMBER)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().clubs?.any { it.id == "club-2" } == true)
    }

    @Test
    fun `invoke fails with UnauthorizedException when user is OWNER`() = runTest {
        val result = useCase(params, Role.OWNER)

        assertTrue(result.isFailure)
        assertIs<UnauthorizedException>(result.exceptionOrNull())
    }

    @Test
    fun `invoke propagates fetch failure`() = runTest {
        everySuspend { memberRepository.getMember("m1", forceRefresh = true) } returns
            Result.failure(RuntimeException("Not found"))

        val result = useCase(params, Role.MEMBER)

        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke propagates update failure`() = runTest {
        everySuspend { memberRepository.getMember("m1", forceRefresh = true) } returns Result.success(selfMember)
        everySuspend { memberRepository.updateMember(memberId = "m1", clubIds = listOf("club-2")) } returns
            Result.failure(RuntimeException("Server error"))

        val result = useCase(params, Role.MEMBER)

        assertTrue(result.isFailure)
    }
}
