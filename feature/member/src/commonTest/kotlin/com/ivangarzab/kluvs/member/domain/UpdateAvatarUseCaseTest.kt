package com.ivangarzab.kluvs.member.domain

import com.ivangarzab.kluvs.data.repositories.AvatarRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.model.Member
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateAvatarUseCaseTest {

    private lateinit var avatarRepository: AvatarRepository
    private lateinit var memberRepository: MemberRepository
    private lateinit var useCase: UpdateAvatarUseCase

    @BeforeTest
    fun setup() {
        avatarRepository = mock<AvatarRepository>()
        memberRepository = mock<MemberRepository>()
        useCase = UpdateAvatarUseCase(avatarRepository, memberRepository)
    }

    @Test
    fun `successful upload and member update returns avatar URL`() = runTest {
        // Given
        val memberId = "member-123"
        val imageData = ByteArray(100) { it.toByte() }
        val oldAvatarPath = "$memberId/old-avatar.png"
        val storagePath = "$memberId/avatar.png"
        val avatarUrl = "https://storage.example.com/$storagePath"
        val existingMember = Member(id = memberId, name = "Test User", avatarPath = oldAvatarPath, points = 0, booksRead = 0)
        val updatedMember = existingMember.copy(avatarPath = storagePath)

        everySuspend { memberRepository.getMember(memberId) } returns Result.success(existingMember)
        everySuspend { avatarRepository.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)
        everySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) } returns Result.success(updatedMember)
        every { avatarRepository.getAvatarUrl(storagePath) } returns avatarUrl
        everySuspend { avatarRepository.deleteAvatar(oldAvatarPath) } returns Result.success(Unit)

        // When
        val result = useCase(memberId, imageData)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(avatarUrl, result.getOrNull())
        verifySuspend { memberRepository.getMember(memberId) }
        verifySuspend { avatarRepository.uploadAvatar(memberId, imageData) }
        verifySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) }
        verify { avatarRepository.getAvatarUrl(storagePath) }
        verifySuspend { avatarRepository.deleteAvatar(oldAvatarPath) }
    }

    @Test
    fun `upload failure returns failure result`() = runTest {
        // Given
        val memberId = "member-123"
        val imageData = ByteArray(100)
        val exception = Exception("Upload failed")
        val existingMember = Member(id = memberId, name = "Test User", avatarPath = null, points = 0, booksRead = 0)

        everySuspend { memberRepository.getMember(memberId) } returns Result.success(existingMember)
        everySuspend { avatarRepository.uploadAvatar(memberId, imageData) } returns Result.failure(exception)

        // When
        val result = useCase(memberId, imageData)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { memberRepository.getMember(memberId) }
        verifySuspend { avatarRepository.uploadAvatar(memberId, imageData) }
        // updateMember and deleteAvatar should not be called — strict mock would fail if called
    }

    @Test
    fun `member update failure returns failure result`() = runTest {
        // Given
        val memberId = "member-123"
        val imageData = ByteArray(100)
        val storagePath = "$memberId/avatar.png"
        val exception = Exception("Failed to update member")
        val existingMember = Member(id = memberId, name = "Test User", avatarPath = null, points = 0, booksRead = 0)

        everySuspend { memberRepository.getMember(memberId) } returns Result.success(existingMember)
        everySuspend { avatarRepository.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)
        everySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) } returns Result.failure(exception)

        // When
        val result = useCase(memberId, imageData)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { memberRepository.getMember(memberId) }
        verifySuspend { avatarRepository.uploadAvatar(memberId, imageData) }
        verifySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) }
        // deleteAvatar should not be called — we return early on update failure
    }

    @Test
    fun `handles null avatar URL gracefully`() = runTest {
        // Given
        val memberId = "member-123"
        val imageData = ByteArray(100)
        val oldAvatarPath = "$memberId/old-avatar.png"
        val storagePath = "$memberId/avatar.png"
        val existingMember = Member(id = memberId, name = "Test User", avatarPath = oldAvatarPath, points = 0, booksRead = 0)
        val updatedMember = existingMember.copy(avatarPath = storagePath)

        everySuspend { memberRepository.getMember(memberId) } returns Result.success(existingMember)
        everySuspend { avatarRepository.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)
        everySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) } returns Result.success(updatedMember)
        every { avatarRepository.getAvatarUrl(storagePath) } returns null
        everySuspend { avatarRepository.deleteAvatar(oldAvatarPath) } returns Result.success(Unit)

        // When
        val result = useCase(memberId, imageData)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("", result.getOrNull()) // Returns empty string if URL is null
        verify { avatarRepository.getAvatarUrl(storagePath) }
    }

    @Test
    fun `first upload with no existing avatar skips deletion`() = runTest {
        // Given
        val memberId = "member-123"
        val imageData = ByteArray(100) { it.toByte() }
        val storagePath = "$memberId/avatar.png"
        val avatarUrl = "https://storage.example.com/$storagePath"
        val existingMember = Member(id = memberId, name = "Test User", avatarPath = null, points = 0, booksRead = 0)
        val updatedMember = existingMember.copy(avatarPath = storagePath)

        everySuspend { memberRepository.getMember(memberId) } returns Result.success(existingMember)
        everySuspend { avatarRepository.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)
        everySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) } returns Result.success(updatedMember)
        every { avatarRepository.getAvatarUrl(storagePath) } returns avatarUrl
        // deleteAvatar is intentionally not mocked — strict mock would fail if it were called

        // When
        val result = useCase(memberId, imageData)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(avatarUrl, result.getOrNull())
    }

    @Test
    fun `old avatar deletion failure does not affect success`() = runTest {
        // Given
        val memberId = "member-123"
        val imageData = ByteArray(100) { it.toByte() }
        val oldAvatarPath = "$memberId/old-avatar.png"
        val storagePath = "$memberId/avatar.png"
        val avatarUrl = "https://storage.example.com/$storagePath"
        val existingMember = Member(id = memberId, name = "Test User", avatarPath = oldAvatarPath, points = 0, booksRead = 0)
        val updatedMember = existingMember.copy(avatarPath = storagePath)

        everySuspend { memberRepository.getMember(memberId) } returns Result.success(existingMember)
        everySuspend { avatarRepository.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)
        everySuspend { memberRepository.updateMember(memberId, avatarPath = storagePath) } returns Result.success(updatedMember)
        every { avatarRepository.getAvatarUrl(storagePath) } returns avatarUrl
        everySuspend { avatarRepository.deleteAvatar(oldAvatarPath) } returns Result.failure(Exception("Delete failed"))

        // When
        val result = useCase(memberId, imageData)

        // Then — deletion failure is non-fatal; use case still succeeds
        assertTrue(result.isSuccess)
        assertEquals(avatarUrl, result.getOrNull())
        verifySuspend { avatarRepository.deleteAvatar(oldAvatarPath) }
    }
}
