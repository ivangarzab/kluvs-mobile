package com.ivangarzab.kluvs.data.repositories

import com.ivangarzab.kluvs.data.remote.source.AvatarRemoteDataSource
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AvatarRepositoryTest {

    private lateinit var remoteDataSource: AvatarRemoteDataSource
    private lateinit var repository: AvatarRepository

    @BeforeTest
    fun setup() {
        remoteDataSource = mock<AvatarRemoteDataSource>()
        repository = AvatarRepositoryImpl(remoteDataSource)
    }

    // ========================================
    // GET AVATAR URL
    // ========================================

    @Test
    fun `getAvatarUrl with null path returns null`() {
        // Given: RemoteDataSource returns null for null path
        every { remoteDataSource.getAvatarUrl(null) } returns null

        // When: Getting avatar URL
        val result = repository.getAvatarUrl(null)

        // Then: Result is null
        assertNull(result)
        verify { remoteDataSource.getAvatarUrl(null) }
    }

    @Test
    fun `getAvatarUrl with valid path returns URL`() {
        // Given: RemoteDataSource returns URL
        val avatarPath = "member-123/avatar.png"
        val expectedUrl = "https://example.supabase.co/storage/v1/object/public/member_avatars/member-123/avatar.png"
        every { remoteDataSource.getAvatarUrl(avatarPath) } returns expectedUrl

        // When: Getting avatar URL
        val result = repository.getAvatarUrl(avatarPath)

        // Then: Result is the URL
        assertEquals(expectedUrl, result)
        verify { remoteDataSource.getAvatarUrl(avatarPath) }
    }

    @Test
    fun `getAvatarUrl delegates to remote data source`() {
        // Given: RemoteDataSource is configured
        val avatarPath = "user-456/avatar.png"
        val url = "https://storage.url/user-456/avatar.png"
        every { remoteDataSource.getAvatarUrl(avatarPath) } returns url

        // When: Calling repository
        repository.getAvatarUrl(avatarPath)

        // Then: RemoteDataSource is called
        verify { remoteDataSource.getAvatarUrl(avatarPath) }
    }

    // ========================================
    // UPLOAD AVATAR
    // ========================================

    @Test
    fun `uploadAvatar success returns storage path`() = runTest {
        // Given: RemoteDataSource returns success
        val memberId = "member-123"
        val imageData = ByteArray(100) { it.toByte() }
        val storagePath = "$memberId/avatar.png"
        everySuspend { remoteDataSource.uploadAvatar(memberId, imageData) } returns Result.success(storagePath)

        // When: Uploading avatar
        val result = repository.uploadAvatar(memberId, imageData)

        // Then: Result is success with path
        assertTrue(result.isSuccess)
        assertEquals(storagePath, result.getOrNull())
        verifySuspend { remoteDataSource.uploadAvatar(memberId, imageData) }
    }

    @Test
    fun `uploadAvatar failure returns Result failure`() = runTest {
        // Given: RemoteDataSource returns failure
        val memberId = "member-123"
        val imageData = ByteArray(100)
        val exception = Exception("Upload failed")
        everySuspend { remoteDataSource.uploadAvatar(memberId, imageData) } returns Result.failure(exception)

        // When: Uploading avatar
        val result = repository.uploadAvatar(memberId, imageData)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.uploadAvatar(memberId, imageData) }
    }

    @Test
    fun `uploadAvatar delegates to remote data source with correct parameters`() = runTest {
        // Given: RemoteDataSource is configured
        val memberId = "user-789"
        val imageData = ByteArray(200) { (it % 256).toByte() }
        everySuspend { remoteDataSource.uploadAvatar(memberId, imageData) } returns Result.success("$memberId/avatar.png")

        // When: Uploading avatar
        repository.uploadAvatar(memberId, imageData)

        // Then: RemoteDataSource is called with correct parameters
        verifySuspend { remoteDataSource.uploadAvatar(memberId, imageData) }
    }

    @Test
    fun `uploadAvatar with large image data succeeds`() = runTest {
        // Given: Large image data
        val memberId = "member-456"
        val largeImageData = ByteArray(500_000) { (it % 256).toByte() } // 500KB
        val storagePath = "$memberId/avatar.png"
        everySuspend { remoteDataSource.uploadAvatar(memberId, largeImageData) } returns Result.success(storagePath)

        // When: Uploading large avatar
        val result = repository.uploadAvatar(memberId, largeImageData)

        // Then: Upload succeeds
        assertTrue(result.isSuccess)
        assertEquals(storagePath, result.getOrNull())
        verifySuspend { remoteDataSource.uploadAvatar(memberId, largeImageData) }
    }

    // ========================================
    // DELETE AVATAR
    // ========================================

    @Test
    fun `deleteAvatar success returns Result success`() = runTest {
        // Given: RemoteDataSource returns success
        val avatarPath = "member-123/1234567890.png"
        everySuspend { remoteDataSource.deleteAvatar(avatarPath) } returns Result.success(Unit)

        // When: Deleting avatar
        val result = repository.deleteAvatar(avatarPath)

        // Then: Result is success
        assertTrue(result.isSuccess)
        verifySuspend { remoteDataSource.deleteAvatar(avatarPath) }
    }

    @Test
    fun `deleteAvatar failure returns Result failure`() = runTest {
        // Given: RemoteDataSource returns failure
        val avatarPath = "member-123/1234567890.png"
        val exception = Exception("Delete failed")
        everySuspend { remoteDataSource.deleteAvatar(avatarPath) } returns Result.failure(exception)

        // When: Deleting avatar
        val result = repository.deleteAvatar(avatarPath)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verifySuspend { remoteDataSource.deleteAvatar(avatarPath) }
    }
}
