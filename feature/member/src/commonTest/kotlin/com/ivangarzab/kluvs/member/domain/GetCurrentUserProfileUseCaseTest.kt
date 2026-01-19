package com.ivangarzab.kluvs.member.domain

import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.kluvs.presentation.util.FormatDateTimeUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetCurrentUserProfileUseCaseTest {

    private lateinit var memberRepository: MemberRepository
    private val formatDateTime = FormatDateTimeUseCase()
    private lateinit var useCase: GetCurrentUserProfileUseCase

    @BeforeTest
    fun setup() {
        memberRepository = mock<MemberRepository>()
        useCase = GetCurrentUserProfileUseCase(memberRepository, formatDateTime)
    }

    @Test
    fun `returns user profile when repository succeeds`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-456",
            name = "John Doe",
            userId = userId,
            role = "admin",
            points = 100,
            booksRead = 15
        )
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result.isSuccess)
        val profile = result.getOrNull()!!
        assertEquals("member-456", profile.memberId)
        assertEquals("John Doe", profile.name)
        assertEquals("@johndoe", profile.handle)
        assertEquals("2025", profile.joinDate) // Placeholder until we add created_at
        assertNull(profile.avatarUrl)
        verifySuspend { memberRepository.getMemberByUserId(userId) }
    }

    @Test
    fun `generates handle from name correctly`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-456",
            name = "Alice Smith",
            userId = userId,
            role = null,
            points = 0,
            booksRead = 0
        )
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("@alicesmith", result.getOrNull()?.handle)
        verifySuspend { memberRepository.getMemberByUserId(userId) }
    }

    @Test
    fun `handles name with multiple spaces`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-456",
            name = "Mary Jane Watson",
            userId = userId,
            role = null,
            points = 0,
            booksRead = 0
        )
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("@maryjanewatson", result.getOrNull()?.handle)
        verifySuspend { memberRepository.getMemberByUserId(userId) }
    }

    @Test
    fun `returns failure when repository fails`() = runTest {
        // Given
        val userId = "user-123"
        val exception = Exception("Member not found")
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.failure(exception)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { memberRepository.getMemberByUserId(userId) }
    }
}
