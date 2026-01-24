import UIKit

/**
 * Compresses and resizes image to fit within constraints.
 *
 * - Parameters:
 *   - imageData: Original image data
 *   - maxDimension: Maximum width/height (default 512)
 *   - maxBytes: Maximum file size in bytes (default 500KB)
 * - Returns: Compressed image data
 */
func compressImage(_ imageData: Data, maxDimension: CGFloat = 512, maxBytes: Int = 500_000) -> Data {
    guard let image = UIImage(data: imageData) else {
        return imageData
    }

    // Calculate new size maintaining aspect ratio
    let size = image.size
    let scale: CGFloat
    if size.width > maxDimension || size.height > maxDimension {
        scale = min(maxDimension / size.width, maxDimension / size.height)
    } else {
        scale = 1.0
    }

    let newSize = CGSize(width: size.width * scale, height: size.height * scale)

    // Resize image
    UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
    image.draw(in: CGRect(origin: .zero, size: newSize))
    guard let resizedImage = UIGraphicsGetImageFromCurrentImageContext() else {
        UIGraphicsEndImageContext()
        return imageData
    }
    UIGraphicsEndImageContext()

    // Compress with decreasing quality until under maxBytes
    var quality: CGFloat = 0.9
    var compressedData = resizedImage.jpegData(compressionQuality: quality) ?? imageData

    while compressedData.count > maxBytes && quality > 0.1 {
        quality -= 0.1
        compressedData = resizedImage.jpegData(compressionQuality: quality) ?? compressedData
    }

    return compressedData
}
