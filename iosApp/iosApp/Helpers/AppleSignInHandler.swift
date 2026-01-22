//
//  AppleSignInHandler.swift
//  iosApp
//
//  Native Apple Sign In handler using AuthenticationServices
//

import AuthenticationServices
import Foundation

/// Handler for native Apple Sign In flow.
/// Uses ASAuthorizationController to present the native Apple Sign In sheet.
class AppleSignInHandler: NSObject, ObservableObject {
    static let shared = AppleSignInHandler()

    @Published var idToken: String?
    @Published var error: Error?
    @Published var isProcessing = false

    private var completionHandler: ((Result<String, Error>) -> Void)?

    private override init() {
        super.init()
    }

    /// Initiates the Apple Sign In flow.
    /// - Parameter completion: Called with the ID token on success, or error on failure.
    func signIn(completion: @escaping (Result<String, Error>) -> Void) {
        self.completionHandler = completion
        self.isProcessing = true
        self.error = nil
        self.idToken = nil

        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]

        let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = self
        controller.presentationContextProvider = self
        controller.performRequests()
    }
}

// MARK: - ASAuthorizationControllerDelegate
extension AppleSignInHandler: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        isProcessing = false

        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential else {
            let error = NSError(domain: "AppleSignIn", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid credential type"])
            self.error = error
            completionHandler?(.failure(error))
            return
        }

        guard let identityTokenData = appleIDCredential.identityToken,
              let identityToken = String(data: identityTokenData, encoding: .utf8) else {
            let error = NSError(domain: "AppleSignIn", code: -2, userInfo: [NSLocalizedDescriptionKey: "Missing identity token"])
            self.error = error
            completionHandler?(.failure(error))
            return
        }

        print("AppleSignInHandler: Successfully obtained identity token")
        self.idToken = identityToken
        completionHandler?(.success(identityToken))
    }

    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        isProcessing = false

        // Check if user cancelled
        if let authError = error as? ASAuthorizationError,
           authError.code == .canceled {
            print("AppleSignInHandler: User cancelled sign in")
            // Don't report cancellation as an error
            self.error = nil
            completionHandler?(.failure(authError))
            return
        }

        print("AppleSignInHandler: Error during sign in: \(error)")
        self.error = error
        completionHandler?(.failure(error))
    }
}

// MARK: - ASAuthorizationControllerPresentationContextProviding
extension AppleSignInHandler: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        // Get the first window scene's key window
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first else {
            fatalError("No window available for Apple Sign In presentation")
        }
        return window
    }
}
