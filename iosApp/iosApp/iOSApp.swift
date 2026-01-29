import SwiftUI
import Shared

@main
struct iOSApp: App {

    init() {
        SentrySetupKt.initializeSentry()
        startLogging()
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
    
    private func startLogging() {
        #if DEBUG
        let volume = Level.verbose
        #else
        let volume = Level.debug
        #endif
        
        BarkConfig().autoTagDisabled = false
        Bark().train(trainer: NSLogTrainer(volume: volume))
        Bark().train(trainer: SentryTrainer())
    }
}
