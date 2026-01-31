import XCTest
import SwiftUI
@testable import Kluvs

/**
 * Tests for color utilities and brand color definitions.
 *
 * Verifies hex color conversion accuracy and brand color values.
 */
class ColorsTests: XCTestCase {
    
    // MARK: - Hex to UIColor Conversion Tests
    
    func testUIColorHexConversion_Red() {
        let color = UIColor(hex: 0xFF0000)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 1.0, accuracy: 0.01)
        XCTAssertEqual(green, 0.0, accuracy: 0.01)
        XCTAssertEqual(blue, 0.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testUIColorHexConversion_Green() {
        let color = UIColor(hex: 0x00FF00)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 0.0, accuracy: 0.01)
        XCTAssertEqual(green, 1.0, accuracy: 0.01)
        XCTAssertEqual(blue, 0.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testUIColorHexConversion_Blue() {
        let color = UIColor(hex: 0x0000FF)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 0.0, accuracy: 0.01)
        XCTAssertEqual(green, 0.0, accuracy: 0.01)
        XCTAssertEqual(blue, 1.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testUIColorHexConversion_Black() {
        let color = UIColor(hex: 0x000000)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 0.0, accuracy: 0.01)
        XCTAssertEqual(green, 0.0, accuracy: 0.01)
        XCTAssertEqual(blue, 0.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testUIColorHexConversion_White() {
        let color = UIColor(hex: 0xFFFFFF)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 1.0, accuracy: 0.01)
        XCTAssertEqual(green, 1.0, accuracy: 0.01)
        XCTAssertEqual(blue, 1.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testUIColorHexConversion_WithAlpha() {
        let color = UIColor(hex: 0xFF0000, alpha: 0.5)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 1.0, accuracy: 0.01)
        XCTAssertEqual(green, 0.0, accuracy: 0.01)
        XCTAssertEqual(blue, 0.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 0.5, accuracy: 0.01)
    }
    
    // MARK: - Hex to SwiftUI Color Conversion Tests
    
    func testColorHexConversion_BrandOrange() {
        let color = Color(hex: 0xD16D30)
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        // D1 = 209/255 ≈ 0.82
        // 6D = 109/255 ≈ 0.43
        // 30 = 48/255 ≈ 0.19
        XCTAssertEqual(red, 209.0/255.0, accuracy: 0.01)
        XCTAssertEqual(green, 109.0/255.0, accuracy: 0.01)
        XCTAssertEqual(blue, 48.0/255.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testColorHexConversion_BrandGreen() {
        let color = Color(hex: 0x48A480)
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        // 48 = 72/255 ≈ 0.28
        // A4 = 164/255 ≈ 0.64
        // 80 = 128/255 ≈ 0.50
        XCTAssertEqual(red, 72.0/255.0, accuracy: 0.01)
        XCTAssertEqual(green, 164.0/255.0, accuracy: 0.01)
        XCTAssertEqual(blue, 128.0/255.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testColorHexConversion_BrandBlue() {
        let color = Color(hex: 0x006781)
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        // 00 = 0/255 = 0.0
        // 67 = 103/255 ≈ 0.40
        // 81 = 129/255 ≈ 0.51
        XCTAssertEqual(red, 0.0/255.0, accuracy: 0.01)
        XCTAssertEqual(green, 103.0/255.0, accuracy: 0.01)
        XCTAssertEqual(blue, 129.0/255.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 1.0, accuracy: 0.01)
    }
    
    func testColorHexConversion_WithOpacity() {
        let color = Color(hex: 0xFF0000, alpha: 0.7)
        let uiColor = UIColor(color)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        XCTAssertEqual(red, 1.0, accuracy: 0.01)
        XCTAssertEqual(green, 0.0, accuracy: 0.01)
        XCTAssertEqual(blue, 0.0, accuracy: 0.01)
        XCTAssertEqual(alpha, 0.7, accuracy: 0.01)
    }
    
    // MARK: - Brand Color Definitions Tests
    
    func testBrandOrangeMatchesExpectedHex() {
        let brandOrange = UIColor(Color.brandOrange)
        let expectedOrange = UIColor(hex: 0xD16D30)
        
        var brandRed: CGFloat = 0, brandGreen: CGFloat = 0, brandBlue: CGFloat = 0
        var expectedRed: CGFloat = 0, expectedGreen: CGFloat = 0, expectedBlue: CGFloat = 0
        
        brandOrange.getRed(&brandRed, green: &brandGreen, blue: &brandBlue, alpha: nil as UnsafeMutablePointer<CGFloat>?)
        expectedOrange.getRed(&expectedRed, green: &expectedGreen, blue: &expectedBlue, alpha: nil as UnsafeMutablePointer<CGFloat>?)
        
        XCTAssertEqual(brandRed, expectedRed, accuracy: 0.01)
        XCTAssertEqual(brandGreen, expectedGreen, accuracy: 0.01)
        XCTAssertEqual(brandBlue, expectedBlue, accuracy: 0.01)
    }
    
    func testBrandGreenMatchesExpectedHex() {
        let brandGreen = UIColor(Color.brandGreen)
        let expectedGreen = UIColor(hex: 0x48A480)
        
        var brandRed: CGFloat = 0, brandGreen_G: CGFloat = 0, brandBlue: CGFloat = 0
        var expectedRed: CGFloat = 0, expectedGreen_G: CGFloat = 0, expectedBlue: CGFloat = 0
        
        brandGreen.getRed(&brandRed, green: &brandGreen_G, blue: &brandBlue, alpha: nil as UnsafeMutablePointer<CGFloat>?)
        expectedGreen.getRed(&expectedRed, green: &expectedGreen_G, blue: &expectedBlue, alpha: nil as UnsafeMutablePointer<CGFloat>?)
        
        XCTAssertEqual(brandRed, expectedRed, accuracy: 0.01)
        XCTAssertEqual(brandGreen_G, expectedGreen_G, accuracy: 0.01)
        XCTAssertEqual(brandBlue, expectedBlue, accuracy: 0.01)
    }
    
    func testBrandBlueMatchesExpectedHex() {
        let brandBlue = UIColor(Color.brandBlue)
        let expectedBlue = UIColor(hex: 0x006781)
        
        var brandRed: CGFloat = 0, brandGreen: CGFloat = 0, brandBlue_B: CGFloat = 0
        var expectedRed: CGFloat = 0, expectedGreen: CGFloat = 0, expectedBlue_B: CGFloat = 0
        
        brandBlue.getRed(&brandRed, green: &brandGreen, blue: &brandBlue_B, alpha: nil as UnsafeMutablePointer<CGFloat>?)
        expectedBlue.getRed(&expectedRed, green: &expectedGreen, blue: &expectedBlue_B, alpha: nil as UnsafeMutablePointer<CGFloat>?)
        
        XCTAssertEqual(brandRed, expectedRed, accuracy: 0.01)
        XCTAssertEqual(brandGreen, expectedGreen, accuracy: 0.01)
        XCTAssertEqual(brandBlue_B, expectedBlue_B, accuracy: 0.01)
    }
    
    func testDiscordBlueMatchesExpectedHex() {
        let discordBlue = UIColor(Color.discordBlue)
        let expectedColor = UIColor(hex: 0x5865F2)

        var actualRed: CGFloat = 0, actualGreen: CGFloat = 0, actualBlue: CGFloat = 0
        var expectedRed: CGFloat = 0, expectedGreen: CGFloat = 0, expectedBlue: CGFloat = 0

        var alphaPlaceholder: CGFloat = 0
        discordBlue.getRed(&actualRed, green: &actualGreen, blue: &actualBlue, alpha: &alphaPlaceholder)
        expectedColor.getRed(&expectedRed, green: &expectedGreen, blue: &expectedBlue, alpha: &alphaPlaceholder)

        XCTAssertEqual(actualRed, expectedRed, accuracy: 0.01)
        XCTAssertEqual(actualGreen, expectedGreen, accuracy: 0.01)
        XCTAssertEqual(actualBlue, expectedBlue, accuracy: 0.01)
    }
}
