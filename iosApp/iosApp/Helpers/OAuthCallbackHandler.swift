//
//  OAuthCallbackHandler.swift
//  iosApp
//
//  Singleton handler for OAuth callbacks
//

import Foundation
import Combine

/// Singleton handler for OAuth callbacks.
/// iOSApp posts callback URLs here, and AuthViewModelWrapper collects them.
class OAuthCallbackHandler: ObservableObject {
    static let shared = OAuthCallbackHandler()

    @Published var callbackUrl: URL?

    private init() {}

    func handleCallback(_ url: URL) {
        // Verify this is an OAuth callback
        guard url.scheme == "kluvs",
              url.host == "auth",
              url.path == "/callback" else {
            return
        }

        print("OAuthCallbackHandler: Received callback: \(url)")
        callbackUrl = url
    }

    func clearCallback() {
        callbackUrl = nil
    }
}
