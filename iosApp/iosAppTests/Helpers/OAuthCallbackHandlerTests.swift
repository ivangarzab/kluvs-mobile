import XCTest
import Combine
@testable import Kluvs

/**
 * Tests for OAuthCallbackHandler singleton.
 *
 * Verifies URL validation, state management, and callback handling.
 */
class OAuthCallbackHandlerTests: XCTestCase {
    
    var handler: OAuthCallbackHandler!
    var cancellables: Set<AnyCancellable>!
    
    override func setUp() {
        super.setUp()
        handler = OAuthCallbackHandler.shared
        cancellables = Set<AnyCancellable>()
        handler.clearCallback() // Reset state before each test
    }
    
    override func tearDown() {
        handler.clearCallback()
        cancellables = nil
        super.tearDown()
    }
    
    // MARK: - Valid URL Tests

    func testHandleCallback_ValidURLWithQueryParams_SetsCallbackUrl() {
        // Given
        let validURL = URL(string: "kluvs://auth/callback?access_token=abc&refresh_token=xyz&state=123")!
        
        // When
        handler.handleCallback(validURL)
        
        // Then
        XCTAssertNotNil(handler.callbackUrl)
        XCTAssertEqual(handler.callbackUrl?.absoluteString, validURL.absoluteString)
    }
    
    // MARK: - Invalid URL Tests
    
    func testHandleCallback_WrongScheme_DoesNotSetCallbackUrl() {
        // Given
        let invalidURL = URL(string: "https://auth/callback")!
        
        // When
        handler.handleCallback(invalidURL)
        
        // Then
        XCTAssertNil(handler.callbackUrl, "Should not accept URL with wrong scheme")
    }
    
    func testHandleCallback_WrongHost_DoesNotSetCallbackUrl() {
        // Given
        let invalidURL = URL(string: "kluvs://oauth/callback")!
        
        // When
        handler.handleCallback(invalidURL)
        
        // Then
        XCTAssertNil(handler.callbackUrl, "Should not accept URL with wrong host")
    }
    
    func testHandleCallback_WrongPath_DoesNotSetCallbackUrl() {
        // Given
        let invalidURL = URL(string: "kluvs://auth/redirect")!
        
        // When
        handler.handleCallback(invalidURL)
        
        // Then
        XCTAssertNil(handler.callbackUrl, "Should not accept URL with wrong path")
    }
    
    func testHandleCallback_MissingPath_DoesNotSetCallbackUrl() {
        // Given
        let invalidURL = URL(string: "kluvs://auth")!
        
        // When
        handler.handleCallback(invalidURL)
        
        // Then
        XCTAssertNil(handler.callbackUrl, "Should not accept URL without path")
    }
    
    func testHandleCallback_CompletelyWrongURL_DoesNotSetCallbackUrl() {
        // Given
        let invalidURL = URL(string: "https://example.com/callback")!
        
        // When
        handler.handleCallback(invalidURL)
        
        // Then
        XCTAssertNil(handler.callbackUrl, "Should not accept completely wrong URL")
    }
    
    // MARK: - Clear Callback Tests
    
    func testClearCallback_RemovesCallbackUrl() {
        // Given
        let validURL = URL(string: "kluvs://auth/callback?code=test123")!
        handler.handleCallback(validURL)
        XCTAssertNotNil(handler.callbackUrl)
        
        // When
        handler.clearCallback()
        
        // Then
        XCTAssertNil(handler.callbackUrl, "Callback URL should be cleared")
    }
    
    func testClearCallback_MultipleCalls_DoesNotCrash() {
        // Given
        let validURL = URL(string: "kluvs://auth/callback")!
        handler.handleCallback(validURL)
        
        // When/Then - should not crash
        handler.clearCallback()
        handler.clearCallback()
        handler.clearCallback()
        
        XCTAssertNil(handler.callbackUrl)
    }
    
    // MARK: - Publisher Tests
    
    func testCallbackUrlPublisher_EmitsChanges() {
        // Given
        let expectation = XCTestExpectation(description: "Publisher emits value")
        var receivedValues: [URL?] = []
        
        handler.$callbackUrl
            .sink { url in
                receivedValues.append(url)
                if receivedValues.count == 3 {
                    expectation.fulfill()
                }
            }
            .store(in: &cancellables)
        
        // When
        let url1 = URL(string: "kluvs://auth/callback?code=1")!
        let url2 = URL(string: "kluvs://auth/callback?code=2")!
        
        handler.handleCallback(url1)
        handler.handleCallback(url2)
        
        wait(for: [expectation], timeout: 1.0)
        
        // Then
        XCTAssertEqual(receivedValues.count, 3) // nil (initial), url1, url2
        XCTAssertNil(receivedValues[0])
        XCTAssertEqual(receivedValues[1]?.absoluteString, url1.absoluteString)
        XCTAssertEqual(receivedValues[2]?.absoluteString, url2.absoluteString)
    }
    
    // MARK: - Singleton Tests
    
    func testSharedInstance_IsSingleton() {
        let instance1 = OAuthCallbackHandler.shared
        let instance2 = OAuthCallbackHandler.shared
        
        XCTAssertTrue(instance1 === instance2, "Should return same singleton instance")
    }
    
    func testSharedInstance_MaintainsState() {
        // Given
        let handler1 = OAuthCallbackHandler.shared
        let validURL = URL(string: "kluvs://auth/callback?code=test")!
        
        // When
        handler1.handleCallback(validURL)
        
        // Then - accessing through different reference should show same state
        let handler2 = OAuthCallbackHandler.shared
        XCTAssertEqual(handler2.callbackUrl?.absoluteString, validURL.absoluteString)
    }
    
    // MARK: - Edge Cases
    
    func testHandleCallback_URLWithFragment_IsHandledCorrectly() {
        // Given - Some OAuth flows use fragments
        let urlWithFragment = URL(string: "kluvs://auth/callback#access_token=abc")!
        
        // When
        handler.handleCallback(urlWithFragment)
        
        // Then
        XCTAssertNotNil(handler.callbackUrl)
        XCTAssertEqual(handler.callbackUrl?.fragment, "access_token=abc")
    }
    
    func testHandleCallback_EmptyQueryParameters_IsValid() {
        // Given
        let urlWithEmptyQuery = URL(string: "kluvs://auth/callback?")!
        
        // When
        handler.handleCallback(urlWithEmptyQuery)
        
        // Then
        XCTAssertNotNil(handler.callbackUrl)
    }
    
}
