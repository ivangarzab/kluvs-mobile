package com.ivangarzab.kluvs.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

/**
 * Compresses and resizes image to fit within constraints.
 *
 * @param imageBytes Original image bytes
 * @param maxDimension Maximum width/height (default 512)
 * @param maxBytes Maximum file size in bytes (default 500KB)
 * @param quality Initial JPEG quality (default 90)
 * @return Compressed image bytes
 */
internal fun compressImage(
    imageBytes: ByteArray,
    maxDimension: Int = 512,
    maxBytes: Int = 500_000,
    quality: Int = 90
): ByteArray {
    // Decode original bitmap
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

    // Calculate sample size for initial downscaling
    val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxDimension)

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    }
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, decodeOptions)
        ?: return imageBytes

    // Scale to exact max dimension
    val scaledBitmap = scaleBitmap(bitmap, maxDimension)
    if (scaledBitmap != bitmap) {
        bitmap.recycle()
    }

    // Compress with decreasing quality until under maxBytes
    var currentQuality = quality
    var outputBytes: ByteArray

    do {
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, currentQuality, outputStream)
        outputBytes = outputStream.toByteArray()
        currentQuality -= 10
    } while (outputBytes.size > maxBytes && currentQuality > 10)

    scaledBitmap.recycle()
    return outputBytes
}

/**
 * Calculates the sample size for initial bitmap downscaling.
 *
 * @param width Original image width
 * @param height Original image height
 * @param maxDimension Target maximum dimension
 * @return Sample size (power of 2)
 */
internal fun calculateSampleSize(width: Int, height: Int, maxDimension: Int): Int {
    var sampleSize = 1
    val maxOriginal = maxOf(width, height)
    while (maxOriginal / sampleSize > maxDimension * 2) {
        sampleSize *= 2
    }
    return sampleSize
}

/**
 * Scales a bitmap to fit within the maximum dimension while maintaining aspect ratio.
 *
 * @param bitmap Original bitmap
 * @param maxDimension Maximum width/height
 * @return Scaled bitmap (or original if already within bounds)
 */
internal fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxDimension && height <= maxDimension) {
        return bitmap
    }

    val scale = minOf(
        maxDimension.toFloat() / width,
        maxDimension.toFloat() / height
    )

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}