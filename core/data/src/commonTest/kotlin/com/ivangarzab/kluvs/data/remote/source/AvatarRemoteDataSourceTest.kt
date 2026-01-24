package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.kluvs.data.remote.api.AvatarService
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AvatarRemoteDataSourceTest {

    private lateinit var avatarService: AvatarService
    private lateinit var dataSource: AvatarRemoteDataSource

    @BeforeTest
    fun setup() {
        avatarService = mock<AvatarService>()
        dataSource = AvatarRemoteDataSourceImpl(avatarService)
    }

    // ========================================
    // GET AVATAR URL
    // ========================================

    @Test
    fun `getAvatarUrl with null path returns null`() {
        // Given: Service returns null for null path
        every { avatarService.getAvatarUrl(null) } returns null

        // When: Getting avatar URL
        val result = dataSource.getAvatarUrl(null)

        // Then: Result is null
        assertNull(result)
        verify { avatarService.getAvatarUrl(null) }
    }

    @Test
    fun `getAvatarUrl with valid path returns URL`() {
        // Given: Service returns URL
        val avatarPath = "member-123/avatar.png"
        val expectedUrl = "https://example.supabase.co/storage/v1/object/public/member_avatars/member-123/avatar.png"
        every { avatarService.getAvatarUrl(avatarPath) } returns expectedUrl

        // When: Getting avatar URL
        val result = dataSource.getAvatarUrl(avatarPath)

        // Then: Result is the URL
        assertEquals(expectedUrl, result)
        verify { avatarService.getAvatarUrl(avatarPath) }
    }

    @Test
    fun `getAvatarUrl delegates to service`() {
        // Given: Service is configured
        val avatarPath = "user-456/avatar.png"
        val url = "https://storage.url/user-456/avatar.png"
        every { avatarService.getAvatarUrl(avatarPath) } returns url

        // When: Calling dataSource
        dataSource.getAvatarUrl(avatarPath)

        // Then: Service is called
        verify { avatarService.getAvatarUrl(avatarPath) }
    }

    // ========================================
    // UPLOAD AVATAR
    // ========================================

    @Test
    fun `uploadAvatar success returns storage path`() = runTest {
        // Given: Service returns success
        val memberId = "member-123"
        val imageData = ByteArray(100) { it.toByte() }
        val storagePath = "$memberId/avatar.png"
        everySuspend { avatarService.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)

        // When: Uploading avatar
        val result = dataSource.uploadAvatar(memberId, imageData)

        // Then: Result is success with path
        assertTrue(result.isSuccess)
        assertEquals(storagePath, result.getOrNull())
        verifySuspend { avatarService.uploadAvatar(memberId, imageData) }
    }

    @Test
    fun `uploadAvatar failure returns Result failure`() = runTest {
        // Given: Service returns failure
        val memberId = "member-123"
        val imageData = ByteArray(100)
        val exception = Exception("Upload failed")
        everySuspend { avatarService.uploadAvatar(memberId, imageData) } returns Result.failure(exception)

        // When: Uploading avatar
        val result = dataSource.uploadAvatar(memberId, imageData)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { avatarService.uploadAvatar(memberId, imageData) }
    }

    @Test
    fun `uploadAvatar catches service exception and returns failure`() = runTest {
        // Given: Service throws exception
        val memberId = "member-123"
        val imageData = ByteArray(100)
        val exception = RuntimeException("Network error")
        everySuspend { avatarService.uploadAvatar(memberId, imageData) } throws exception

        // When: Uploading avatar
        val result = dataSource.uploadAvatar(memberId, imageData)

        // Then: Result is failure with caught exception
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { avatarService.uploadAvatar(memberId, imageData) }
    }

    @Test
    fun `uploadAvatar delegates to service with correct parameters`() = runTest {
        // Given: Service is configured
        val memberId = "user-789"
        val imageData = ByteArray(200) { (it % 256).toByte() }
        everySuspend { avatarService.uploadAvatar(memberId, imageData) } returns Result.success("$memberId/avatar.png")

        // When: Uploading avatar
        dataSource.uploadAvatar(memberId, imageData)

        // Then: Service is called with correct parameters
        verifySuspend { avatarService.uploadAvatar(memberId, imageData) }
    }
}
