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
        val expectedPath = "$memberId/avatar.png"

        try {
            // ==========================================
            // 1. Test Upload
            // ==========================================

            // When: Uploading avatar
            println("Attempting upload to bucket '$bucketName' with path '$expectedPath'...")
            val uploadResult = avatarService.uploadAvatar(memberId, dummyImageBytes)

            // Then: Result should be success and return the correct path
            if (uploadResult.isFailure) {
                val error = uploadResult.exceptionOrNull()
                println("✗ Upload FAILED")
                println("  Error message: ${error?.message}")
//                println("  Error type: ${error?.javaClass?.simpleName}")
                println("  Stack trace:")
                error?.printStackTrace()

                // Try to get more details from the error
                if (error?.message?.contains("404") == true) {
                    println("\n  → This suggests the bucket doesn't exist or the path is wrong")
                } else if (error?.message?.contains("403") == true || error?.message?.contains("Unauthorized") == true) {
                    println("\n  → This suggests a permissions issue")
                }
            } else {
                println("✓ Upload succeeded")
            }
            assertTrue(uploadResult.isSuccess, "Upload should return success. Error: ${uploadResult.exceptionOrNull()?.message}")
            assertEquals(expectedPath, uploadResult.getOrNull())

            // Verify: Check if file actually exists in Supabase Storage using the raw client
            // (This confirms the integration actually touched the server)
            val files = supabaseClient.storage.from(bucketName).list(memberId)
            assertTrue(files.any { it.name == "avatar.png" }, "File should exist in the bucket folder")

            // ==========================================
            // 2. Test Get URL
            // ==========================================

            // When: requesting the public URL
            val url = avatarService.getAvatarUrl(expectedPath)

            // Then: URL should be formed correctly
            assertNotNull(url)
            assertTrue(url.startsWith(BuildKonfig.TEST_SUPABASE_URL), "URL should start with supabase URL")
            assertTrue(url.contains("/storage/v1/object/public/$bucketName/$expectedPath"), "URL should point to the specific public asset")

        } finally {
            // Cleanup: Remove the test file so the test is repeatable
            try {
                supabaseClient.storage.from(bucketName).delete(listOf(expectedPath))
                println("Successfully cleaned up test file: $expectedPath")
            } catch (e: Exception) {
                println("Warning: Failed to clean up test avatar: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    @Test
    fun testOverwriteAvatar() = runTest {
        // Given: An existing file
        val memberId = "test-integration-overwrite"
        val data1 = byteArrayOf(1, 2, 3)
        val data2 = byteArrayOf(4, 5, 6)

        try {
            // Upload first version
            val firstUpload = avatarService.uploadAvatar(memberId, data1)
            if (firstUpload.isFailure) {
                println("First upload failed: ${firstUpload.exceptionOrNull()?.message}")
                firstUpload.exceptionOrNull()?.printStackTrace()
            }
            assertTrue(firstUpload.isSuccess, "First upload failed: ${firstUpload.exceptionOrNull()?.message}")

            // When: Uploading different data to the same member ID
            val result = avatarService.uploadAvatar(memberId, data2)

            // Then: Should succeed (because upsert = true in implementation)
            if (result.isFailure) {
                println("Second upload failed: ${result.exceptionOrNull()?.message}")
                result.exceptionOrNull()?.printStackTrace()
            }
            assertTrue(result.isSuccess, "Second upload failed: ${result.exceptionOrNull()?.message}")

            // Verify: Download to ensure content changed
            val downloadedBytes = supabaseClient.storage.from(bucketName)
                .downloadPublic("$memberId/avatar.png")

            // Note: simple content check
            assertEquals(data2.size, downloadedBytes.size)
            assertEquals(data2[0], downloadedBytes[0])

        } finally {
            try {
                supabaseClient.storage.from(bucketName).delete(listOf("$memberId/avatar.png"))
                println("Successfully cleaned up overwrite test file")
            } catch (e: Exception) {
                println("Warning: Failed to clean up overwrite test: ${e.message}")
            }
        }
    }
}