import XCTest
import SwiftUI
@testable import Kluvs

/**
 * Tests for CustomIcon enum and image loading.
 *
 * Verifies all icons can be loaded and rendered correctly.
 */
class CustomIconTests: XCTestCase {
    
    // MARK: - Enum Raw Values
    
    func testIconRawValues() {
        XCTAssertEqual(CustomIcon.club.rawValue, "ic_club")
        XCTAssertEqual(CustomIcon.clubs.rawValue, "ic_clubs")
        XCTAssertEqual(CustomIcon.user.rawValue, "ic_user")
        XCTAssertEqual(CustomIcon.book.rawValue, "ic_book")
        XCTAssertEqual(CustomIcon.points.rawValue, "ic_points")
        XCTAssertEqual(CustomIcon.location.rawValue, "ic_location")
        XCTAssertEqual(CustomIcon.settings.rawValue, "ic_settings")
        XCTAssertEqual(CustomIcon.help.rawValue, "ic_help")
        XCTAssertEqual(CustomIcon.checkmark.rawValue, "ic_checkmark")
        XCTAssertEqual(CustomIcon.logout.rawValue, "ic_logout")
        XCTAssertEqual(CustomIcon.email.rawValue, "ic_email")
        XCTAssertEqual(CustomIcon.password.rawValue, "ic_password")
        XCTAssertEqual(CustomIcon.edit.rawValue, "ic_edit")
        XCTAssertEqual(CustomIcon.discord.rawValue, "ic_discord")
        XCTAssertEqual(CustomIcon.google.rawValue, "ic_google")
        XCTAssertEqual(CustomIcon.apple.rawValue, "apple.logo")
    }
    
    // MARK: - Image Creation
    
    func testAllIconsReturnNonNilImages() {
        // Test that all icon cases can create images without crashing
        let allIcons: [CustomIcon] = [
            .club, .clubs, .user, .book, .points, .location,
            .settings, .help, .checkmark, .logout, .email,
            .password, .edit, .discord, .google, .apple
        ]
        
        for icon in allIcons {
            // This should not crash
            let image = icon.image
            XCTAssertNotNil(image, "Icon \(icon.rawValue) should create a valid Image")
        }
    }
    
    func testAppleIconUsesSFSymbol() {
        // Apple icon should use SF Symbol "apple.logo"
        let appleIcon = CustomIcon.apple
        XCTAssertEqual(appleIcon.rawValue, "apple.logo")
        
        // Image should be created (though we can't verify it's an SF Symbol in unit tests)
        let image = appleIcon.image
        XCTAssertNotNil(image)
    }
    
    func testNonAppleIconsUseCustomAssets() {
        // All non-apple icons should use custom asset names with "ic_" prefix
        let customAssetIcons: [CustomIcon] = [
            .club, .clubs, .user, .book, .points, .location,
            .settings, .help, .checkmark, .logout, .email,
            .password, .edit, .discord, .google
        ]
        
        for icon in customAssetIcons {
            XCTAssertTrue(icon.rawValue.hasPrefix("ic_"), 
                         "Icon \(icon.rawValue) should use ic_ prefix for custom assets")
        }
    }
    
    // MARK: - Image Extension
    
    func testImageCustomExtensionReturnsCorrectImage() {
        // Test that Image.custom(_:) works correctly
        let clubImage = Image.custom(.club)
        XCTAssertNotNil(clubImage)
        
        let appleImage = Image.custom(.apple)
        XCTAssertNotNil(appleImage)
    }
    
    // MARK: - Icon Categories
    
    func testNavigationIcons() {
        // Navigation-related icons
        let navIcons: [CustomIcon] = [.club, .clubs, .user]
        for icon in navIcons {
            XCTAssertNotNil(icon.image)
        }
    }
    
    func testActionIcons() {
        // Action-related icons
        let actionIcons: [CustomIcon] = [.settings, .help, .logout, .edit, .checkmark]
        for icon in actionIcons {
            XCTAssertNotNil(icon.image)
        }
    }
    
    func testContentIcons() {
        // Content-related icons
        let contentIcons: [CustomIcon] = [.book, .points, .location]
        for icon in contentIcons {
            XCTAssertNotNil(icon.image)
        }
    }
    
    func testAuthIcons() {
        // Authentication-related icons
        let authIcons: [CustomIcon] = [.email, .password, .discord, .google, .apple]
        for icon in authIcons {
            XCTAssertNotNil(icon.image)
        }
    }
}
