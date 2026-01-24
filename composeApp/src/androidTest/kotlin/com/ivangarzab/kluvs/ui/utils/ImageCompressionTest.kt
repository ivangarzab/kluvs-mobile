package com.ivangarzab.kluvs.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivangarzab.kluvs.ui.utils.calculateSampleSize
import com.ivangarzab.kluvs.ui.utils.compressImage
import com.ivangarzab.kluvs.ui.utils.scaleBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

/**
 * Instrumented tests for image compression functions.
 *
 * These tests run on an Android device/emulator to verify the compression logic
 * works correctly with real Android Bitmap APIs.
 */
@RunWith(AndroidJUnit4::class)
class ImageCompressionTest {

    /**
     * Creates a test bitmap of specified dimensions with a solid color.
     */
    private fun createTestBitmap(width: Int, height: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Fill with a solid color (optional, but makes it more realistic)
        bitmap.eraseColor(Color.BLUE)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        bitmap.recycle()
        return outputStream.toByteArray()
    }

    @Test
    fun compressImage_largeImage_scaledDownToMaxDimension() {
        // Create a 2048x2048 image
        val largeImageBytes = createTestBitmap(2048, 2048)

        // Compress with maxDimension=512
        val compressedBytes = compressImage(largeImageBytes, maxDimension = 512)

        // Decode the result and verify dimensions
        val resultBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
        assertEquals(512, resultBitmap.width)
        assertEquals(512, resultBitmap.height)
        resultBitmap.recycle()
    }

    @Test
    fun compressImage_smallImage_notScaledUp() {
        // Create a 256x256 image (smaller than maxDimension)
        val smallImageBytes = createTestBitmap(256, 256)

        // Compress with maxDimension=512
        val compressedBytes = compressImage(smallImageBytes, maxDimension = 512)

        // Decode the result and verify dimensions didn't increase
        val resultBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
        assertEquals(256, resultBitmap.width)
        assertEquals(256, resultBitmap.height)
        resultBitmap.recycle()
    }

    @Test
    fun compressImage_rectangularImage_maintainsAspectRatio() {
        // Create a 1024x768 image (4:3 aspect ratio)
        val rectangularImageBytes = createTestBitmap(1024, 768)

        // Compress with maxDimension=512
        val compressedBytes = compressImage(rectangularImageBytes, maxDimension = 512)

        // Decode the result and verify aspect ratio is maintained
        val resultBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
        assertEquals(512, resultBitmap.width)
        assertEquals(384, resultBitmap.height) // 512 * (3/4) = 384
        resultBitmap.recycle()
    }

    @Test
    fun compressImage_compressesToTargetSize() {
        // Create a large image
        val largeImageBytes = createTestBitmap(2048, 2048)

        // Compress with maxBytes=100KB
        val compressedBytes = compressImage(largeImageBytes, maxBytes = 100_000)

        // Verify the result is under the target size
        assertTrue(
            "Compressed size (${compressedBytes.size}) should be <= 100KB",
            compressedBytes.size <= 100_000
        )
    }

    @Test
    fun calculateSampleSize_returnsCorrectPowerOf2() {
        // For a 2048x2048 image with maxDimension=512
        // We want to get it to around maxDimension * 2 (1024)
        // 2048 / 1 = 2048 > 1024, so continue
        // 2048 / 2 = 1024 NOT > 1024, so stop
        // Result: sampleSize = 2, giving us 1024x1024 for final scaling
        val sampleSize = calculateSampleSize(2048, 2048, 512)
        assertEquals(2, sampleSize)
    }

    @Test
    fun calculateSampleSize_smallImage_returnsOne() {
        // For a 256x256 image with maxDimension=512
        val sampleSize = calculateSampleSize(256, 256, 512)
        assertEquals(1, sampleSize)
    }

    @Test
    fun scaleBitmap_largeImage_scaledDown() {
        val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)

        val scaledBitmap = scaleBitmap(bitmap, maxDimension = 512)

        assertEquals(512, scaledBitmap.width)
        assertEquals(512, scaledBitmap.height)

        bitmap.recycle()
        scaledBitmap.recycle()
    }

    @Test
    fun scaleBitmap_smallImage_notScaled() {
        val bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)

        val scaledBitmap = scaleBitmap(bitmap, maxDimension = 512)

        // Should return the same bitmap instance
        assertEquals(bitmap, scaledBitmap)
        assertEquals(256, scaledBitmap.width)
        assertEquals(256, scaledBitmap.height)

        bitmap.recycle()
    }

    @Test
    fun scaleBitmap_rectangularImage_maintainsAspectRatio() {
        val bitmap = Bitmap.createBitmap(1600, 1200, Bitmap.Config.ARGB_8888)

        val scaledBitmap = scaleBitmap(bitmap, maxDimension = 800)

        assertEquals(800, scaledBitmap.width)
        assertEquals(600, scaledBitmap.height) // 800 * (3/4) = 600

        bitmap.recycle()
        scaledBitmap.recycle()
    }
}
