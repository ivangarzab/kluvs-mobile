import SwiftUI
import Shared

@main
struct iOSApp: App {

    init() {
        KoinHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .tint(.brandOrange)
                .onOpenURL { url in
                    OAuthCallbackHandler.shared.handleCallback(url)
                }
        }
    }
}
