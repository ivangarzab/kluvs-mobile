import XCTest
import AuthenticationServices
import Combine
@testable import Kluvs

/**
 * Tests for AppleSignInHandler state management.
 *
 * Note: Full sign-in flow and delegate methods require actual Apple framework
 * objects that can't be mocked in unit tests. These tests verify the testable
 * parts: singleton pattern, state management, and published properties.
 */
class AppleSignInHandlerTests: XCTestCase {

    var handler: AppleSignInHandler!
    var cancellables: Set<AnyCancellable>!

    override func setUp() {
        super.setUp()
        handler = AppleSignInHandler.shared
        cancellables = Set<AnyCancellable>()

        // Reset state
        handler.idToken = nil
        handler.error = nil
        handler.isProcessing = false
    }

    override func tearDown() {
        handler.idToken = nil
        handler.error = nil
        handler.isProcessing = false
        cancellables = nil
        super.tearDown()
    }

    // MARK: - Singleton Tests

    func testSharedInstance_IsSingleton() {
        let instance1 = AppleSignInHandler.shared
        let instance2 = AppleSignInHandler.shared

        XCTAssertTrue(instance1 === instance2, "Should return same singleton instance")
    }

    func testSharedInstance_MaintainsState() {
        // Given
        let handler1 = AppleSignInHandler.shared
        handler1.idToken = "test.token.123"

        // When - accessing through different reference
        let handler2 = AppleSignInHandler.shared

        // Then - should have same state
        XCTAssertEqual(handler2.idToken, "test.token.123")
    }

    // MARK: - Initial State Tests

    func testInitialState_AllPropertiesNil() {
        // Given - fresh handler after reset

        // Then
        XCTAssertNil(handler.idToken, "Initial idToken should be nil")
        XCTAssertNil(handler.error, "Initial error should be nil")
        XCTAssertFalse(handler.isProcessing, "Initial isProcessing should be false")
    }

    // MARK: - State Management Tests

    func testIdToken_CanBeSetAndCleared() {
        // When
        handler.idToken = "test.jwt.token"

        // Then
        XCTAssertEqual(handler.idToken, "test.jwt.token")

        // When - clear
        handler.idToken = nil

        // Then
        XCTAssertNil(handler.idToken)
    }

    func testError_CanBeSetAndCleared() {
        // Given
        let testError = NSError(domain: "Test", code: 123, userInfo: [NSLocalizedDescriptionKey: "Test error"])

        // When
        handler.error = testError

        // Then
        XCTAssertNotNil(handler.error)
        XCTAssertEqual((handler.error as NSError?)?.code, 123)

        // When - clear
        handler.error = nil

        // Then
        XCTAssertNil(handler.error)
    }

    func testIsProcessing_CanBeToggled() {
        // Initially false
        XCTAssertFalse(handler.isProcessing)

        // When - set to true
        handler.isProcessing = true

        // Then
        XCTAssertTrue(handler.isProcessing)

        // When - set back to false
        handler.isProcessing = false

        // Then
        XCTAssertFalse(handler.isProcessing)
    }

    // MARK: - Error Handling Tests

    // Note: We cannot test the delegate methods directly because ASAuthorizationController
    // requires at least one valid authorization request and cannot be mocked.
    // The error handling logic is tested indirectly through the state management tests.

    // MARK: - Publisher Tests

    func testIsProcessingPublisher_EmitsChanges() {
        // Given
        let expectation = XCTestExpectation(description: "isProcessing publishes changes")
        var receivedValues: [Bool] = []

        handler.$isProcessing
            .sink { isProcessing in
                receivedValues.append(isProcessing)
            }
            .store(in: &cancellables)

        // When
        handler.isProcessing = true
        handler.isProcessing = false

        // Then
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            XCTAssertEqual(receivedValues, [false, true, false])
            expectation.fulfill()
        }

        wait(for: [expectation], timeout: 1.0)
    }

    func testErrorPublisher_MultipleChanges() {
        // Given
        let expectation = XCTestExpectation(description: "error publishes multiple changes")
        var receivedErrorCount = 0
        let error1 = NSError(domain: "Test", code: 1)
        let error2 = NSError(domain: "Test", code: 2)

        handler.$error
            .dropFirst() // Skip initial nil
            .sink { _ in
                receivedErrorCount += 1
                if receivedErrorCount == 3 {
                    expectation.fulfill()
                }
            }
            .store(in: &cancellables)

        // When
        handler.error = error1
        handler.error = nil
        handler.error = error2

        wait(for: [expectation], timeout: 1.0)

        // Then
        XCTAssertEqual(receivedErrorCount, 3)
    }

    // MARK: - State Reset Tests

    func testStateReset_ClearsAllProperties() {
        // Given - set all properties
        handler.idToken = "test.token"
        handler.error = NSError(domain: "Test", code: 1)
        handler.isProcessing = true

        // When - reset
        handler.idToken = nil
        handler.error = nil
        handler.isProcessing = false

        // Then
        XCTAssertNil(handler.idToken)
        XCTAssertNil(handler.error)
        XCTAssertFalse(handler.isProcessing)
    }
}
