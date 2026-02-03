package com.ivangarzab.kluvs.data.remote.api

import com.ivangarzab.kluvs.network.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.resumable.CachePair
import io.github.jan.supabase.storage.resumable.Fingerprint
import io.github.jan.supabase.storage.resumable.ResumableCache
import io.github.jan.supabase.storage.resumable.ResumableCacheEntry
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AvatarServiceIntegrationTest {

    private lateinit var avatarService: AvatarService
    // We keep a reference to the raw supabase client to verify uploads/perform cleanup
    private lateinit var supabaseClient: SupabaseClient

    // Defined in AvatarServiceImpl.kt companion object
    private val bucketName = AvatarServiceImpl.BUCKET

    @BeforeTest
    fun setup() {
        val url = BuildKonfig.TEST_SUPABASE_URL
        val key = BuildKonfig.TEST_SUPABASE_KEY

        supabaseClient = createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key,
        ) {
            install(Storage) {
                // FIX: Override the default cache to prevent the NPE
                resumable {
                    cache = object : ResumableCache {
                        // In-memory storage using the library's specific types
                        val memory = mutableMapOf<Fingerprint, ResumableCacheEntry>()
                        override suspend fun set(fingerprint: Fingerprint, entry: ResumableCacheEntry) {
                            memory[fingerprint] = entry
                        }
                        override suspend fun get(fingerprint: Fingerprint): ResumableCacheEntry? {
                            return memory[fingerprint]
                        }
                        override suspend fun remove(fingerprint: Fingerprint) {
                            memory.remove(fingerprint)
                        }
                        override suspend fun clear() {
                            memory.clear()
                        }
                        override suspend fun entries(): List<CachePair> {
                            // Convert the map entries to the library's CachePair type
                            return memory.map { CachePair(it.key, it.value) }
                        }
                    }
                }
            }
        }

        avatarService = AvatarServiceImpl(supabaseClient)
    }

    @Test
    fun testDiagnosticBucketSetup() = runTest {
        println("=== DIAGNOSTIC TEST ===")
        println("Supabase URL: ${BuildKonfig.TEST_SUPABASE_URL}")
        println("Bucket name: $bucketName")

        try {
            // Try to list all buckets
            println("Attempting to list buckets...")
            val buckets = supabaseClient.storage.retrieveBuckets()
            println("Available buckets: ${buckets.map { it.name }}")

            val targetBucket = buckets.find { it.name == bucketName }
            if (targetBucket != null) {
                println("✓ Found bucket: $bucketName")
                println("  Public: ${targetBucket.public}")
            } else {
                println("✗ Bucket '$bucketName' NOT FOUND!")
                println("  Available buckets: ${buckets.map { it.name }}")
            }

            // Try to list contents of the bucket
            println("\nAttempting to list bucket contents...")
            val files = supabaseClient.storage.from(bucketName).list()
            println("Files in bucket: ${files.size} files")

        } catch (e: Exception) {
            println("✗ Diagnostic failed: ${e.message}")
            e.printStackTrace()
        }
        println("=== END DIAGNOSTIC ===\n")
    }

    @Test
    fun testGetAvatarUrlWithNullPath() {
        // When: passing null path
        val url = avatarService.getAvatarUrl(null)

        // Then: should return null (no network call needed)
        assertEquals(null, url)
    }

    @Test
    fun testUploadAndGetAvatarUrl() = runTest {
        // Given: A specific member ID and some dummy image bytes
        val memberId = "test-integration-member"
        val dummyImageBytes = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()) // Fake PNG header
        var uploadedPath: String? = null

        try {
            // ==========================================
            // 1. Test Upload
            // ==========================================

            println("Attempting upload to bucket '$bucketName' for member '$memberId'...")
            val uploadResult = avatarService.uploadAvatar(memberId, dummyImageBytes)

            if (uploadResult.isFailure) {
                val error = uploadResult.exceptionOrNull()
                println("✗ Upload FAILED")
                println("  Error message: ${error?.message}")
                println("  Stack trace:")
                error?.printStackTrace()

                if (error?.message?.contains("404") == true) {
                    println("\n  → This suggests the bucket doesn't exist or the path is wrong")
                } else if (error?.message?.contains("403") == true || error?.message?.contains("Unauthorized") == true) {
                    println("\n  → This suggests a permissions issue")
                }
            } else {
                println("✓ Upload succeeded")
            }
            assertTrue(uploadResult.isSuccess, "Upload should return success. Error: ${uploadResult.exceptionOrNull()?.message}")

            // Path is now $memberId/$timestamp.png — verify structure, not exact value
            val path = uploadResult.getOrNull()
            assertNotNull(path)
            uploadedPath = path
            assertTrue(path.startsWith("$memberId/"), "Path should be under the member's folder")
            assertTrue(path.endsWith(".png"), "Path should end with .png")

            // Verify: file actually exists in Supabase Storage
            val fileName = path.substringAfter("$memberId/")
            val files = supabaseClient.storage.from(bucketName).list(memberId)
            assertTrue(files.any { it.name == fileName }, "File '$fileName' should exist in the bucket folder")

            // ==========================================
            // 2. Test Get URL
            // ==========================================

            val url = avatarService.getAvatarUrl(uploadedPath)

            assertNotNull(url)
            assertTrue(url.startsWith(BuildKonfig.TEST_SUPABASE_URL), "URL should start with supabase URL")
            assertTrue(url.contains("/storage/v1/object/public/$bucketName/$uploadedPath"), "URL should point to the specific public asset")

        } finally {
            // Cleanup: Remove the test file so the test is repeatable
            uploadedPath?.let {
                try {
                    supabaseClient.storage.from(bucketName).delete(listOf(it))
                    println("Successfully cleaned up test file: $it")
                } catch (e: Exception) {
                    println("Warning: Failed to clean up test avatar: ${e.message}")
                }
            }
        }
    }

    @Test
    fun testSuccessiveUploadsCreateUniqueFiles() = runTest {
        // Given: Two uploads for the same member
        val memberId = "test-integration-successive"
        val data1 = byteArrayOf(1, 2, 3)
        val data2 = byteArrayOf(4, 5, 6)
        val uploadedPaths = mutableListOf<String>()

        try {
            // Upload first version
            val firstUpload = avatarService.uploadAvatar(memberId, data1)
            assertTrue(firstUpload.isSuccess, "First upload failed: ${firstUpload.exceptionOrNull()?.message}")
            uploadedPaths.add(firstUpload.getOrThrow())

            // Upload second version
            val secondUpload = avatarService.uploadAvatar(memberId, data2)
            assertTrue(secondUpload.isSuccess, "Second upload failed: ${secondUpload.exceptionOrNull()?.message}")
            uploadedPaths.add(secondUpload.getOrThrow())

            // Then: Each upload produced a unique path (different timestamps)
            assertNotEquals(uploadedPaths[0], uploadedPaths[1], "Each upload should produce a unique path")

            // Verify: both files coexist in storage
            val files = supabaseClient.storage.from(bucketName).list(memberId)
            val fileNames = files.map { it.name }
            assertTrue(fileNames.contains(uploadedPaths[0].substringAfter("$memberId/")), "First file should still exist")
            assertTrue(fileNames.contains(uploadedPaths[1].substringAfter("$memberId/")), "Second file should exist")

        } finally {
            if (uploadedPaths.isNotEmpty()) {
                try {
                    supabaseClient.storage.from(bucketName).delete(uploadedPaths)
                    println("Successfully cleaned up successive upload test files")
                } catch (e: Exception) {
                    println("Warning: Failed to clean up successive upload test: ${e.message}")
                }
            }
        }
    }

    @Test
    fun testDeleteAvatar() = runTest {
        // Given: An uploaded avatar
        val memberId = "test-integration-delete"
        val dummyImageBytes = byteArrayOf(1, 2, 3, 4)
        var uploadedPath: String? = null

        try {
            val uploadResult = avatarService.uploadAvatar(memberId, dummyImageBytes)
            assertTrue(uploadResult.isSuccess, "Upload failed: ${uploadResult.exceptionOrNull()?.message}")
            val path = uploadResult.getOrThrow()
            uploadedPath = path

            // Verify it exists before deletion
            val fileName = path.substringAfter("$memberId/")
            val filesBefore = supabaseClient.storage.from(bucketName).list(memberId)
            assertTrue(filesBefore.any { it.name == fileName }, "File should exist before deletion")

            // When: Deleting the avatar
            val deleteResult = avatarService.deleteAvatar(path)

            // Then: Delete should succeed and file should be gone
            assertTrue(deleteResult.isSuccess, "Delete failed: ${deleteResult.exceptionOrNull()?.message}")
            val filesAfter = supabaseClient.storage.from(bucketName).list(memberId)
            assertTrue(filesAfter.none { it.name == fileName }, "File should not exist after deletion")

            // Successfully cleaned up by the test itself
            uploadedPath = null

        } finally {
            // Cleanup in case the test failed before the delete step
            uploadedPath?.let {
                try {
                    supabaseClient.storage.from(bucketName).delete(listOf(it))
                } catch (e: Exception) {
                    println("Warning: Failed to clean up delete test: ${e.message}")
                }
            }
        }
    }
}