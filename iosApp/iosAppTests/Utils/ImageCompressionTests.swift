import XCTest
@testable import Kluvs

/**
 * Tests for image compression functions.
 *
 * These tests verify the compression logic works correctly with UIKit APIs.
 */
class ImageCompressionTests: XCTestCase {

    /**
     * Creates a test UIImage of specified dimensions with a solid color.
     */
    private func createTestImage(width: CGFloat, height: CGFloat) -> Data {
        let size = CGSize(width: width, height: height)

        // Explicitly set scale to 1.0 to avoid device-dependent scaling
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1.0
        let renderer = UIGraphicsImageRenderer(size: size, format: format)

        let image = renderer.image { context in
            // Fill with a solid color
            UIColor.blue.setFill()
            context.fill(CGRect(origin: .zero, size: size))
        }

        // Convert to PNG data
        return image.pngData() ?? Data()
    }

    func testCompressImage_largeImage_scaledDownToMaxDimension() {
        // Create a 2048x2048 image
        let largeImageData = createTestImage(width: 2048, height: 2048)

        // Compress with maxDimension=512
        let compressedData = compressImage(largeImageData, maxDimension: 512)

        // Decode the result and verify dimensions
        guard let resultImage = UIImage(data: compressedData) else {
            XCTFail("Failed to decode compressed image")
            return
        }

        XCTAssertEqual(resultImage.size.width, 512, accuracy: 1.0)
        XCTAssertEqual(resultImage.size.height, 512, accuracy: 1.0)
    }

    func testCompressImage_smallImage_notScaledUp() {
        // Create a 256x256 image (smaller than maxDimension)
        let smallImageData = createTestImage(width: 256, height: 256)

        // Compress with maxDimension=512
        let compressedData = compressImage(smallImageData, maxDimension: 512)

        // Decode the result and verify dimensions didn't increase
        guard let resultImage = UIImage(data: compressedData) else {
            XCTFail("Failed to decode compressed image")
            return
        }

        XCTAssertEqual(resultImage.size.width, 256, accuracy: 1.0)
        XCTAssertEqual(resultImage.size.height, 256, accuracy: 1.0)
    }

    func testCompressImage_rectangularImage_maintainsAspectRatio() {
        // Create a 1024x768 image (4:3 aspect ratio)
        let rectangularImageData = createTestImage(width: 1024, height: 768)

        // Compress with maxDimension=512
        let compressedData = compressImage(rectangularImageData, maxDimension: 512)

        // Decode the result and verify aspect ratio is maintained
        guard let resultImage = UIImage(data: compressedData) else {
            XCTFail("Failed to decode compressed image")
            return
        }

        XCTAssertEqual(resultImage.size.width, 512, accuracy: 1.0)
        XCTAssertEqual(resultImage.size.height, 384, accuracy: 1.0) // 512 * (3/4) = 384
    }

    func testCompressImage_compressesToTargetSize() {
        // Create a large image
        let largeImageData = createTestImage(width: 2048, height: 2048)

        // Compress with maxBytes=100KB
        let compressedData = compressImage(largeImageData, maxBytes: 100_000)

        // Verify the result is under the target size
        XCTAssertLessThanOrEqual(
            compressedData.count,
            100_000,
            "Compressed size (\(compressedData.count)) should be <= 100KB"
        )
    }

    func testCompressImage_verySmallImage_doesNotCrash() {
        // Create a very small 10x10 image
        let tinyImageData = createTestImage(width: 10, height: 10)

        // Should not crash
        let compressedData = compressImage(tinyImageData, maxDimension: 512)

        // Verify it returns valid data
        XCTAssertGreaterThan(compressedData.count, 0)

        // Verify it can be decoded
        guard let resultImage = UIImage(data: compressedData) else {
            XCTFail("Failed to decode compressed tiny image")
            return
        }

        XCTAssertEqual(resultImage.size.width, 10, accuracy: 1.0)
        XCTAssertEqual(resultImage.size.height, 10, accuracy: 1.0)
    }

    func testCompressImage_tallRectangle_maintainsAspectRatio() {
        // Create a 600x1200 image (portrait, 1:2 aspect ratio)
        let tallImageData = createTestImage(width: 600, height: 1200)

        // Compress with maxDimension=512
        let compressedData = compressImage(tallImageData, maxDimension: 512)

        // Decode the result and verify aspect ratio is maintained
        guard let resultImage = UIImage(data: compressedData) else {
            XCTFail("Failed to decode compressed image")
            return
        }

        // Height should be constrained to 512, width should scale proportionally
        XCTAssertEqual(resultImage.size.width, 256, accuracy: 1.0) // 512 * (1/2) = 256
        XCTAssertEqual(resultImage.size.height, 512, accuracy: 1.0)
    }

    func testCompressImage_invalidData_returnsOriginalData() {
        // Create invalid image data
        let invalidData = "not an image".data(using: .utf8)!

        // Should return the original data without crashing
        let result = compressImage(invalidData)

        XCTAssertEqual(result, invalidData)
    }
}
