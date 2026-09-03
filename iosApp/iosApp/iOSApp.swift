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
            // Incoming URLs (OAuth callbacks and invite Universal Links) are handled inside
            // ContentView, which owns the coordinators they have to be routed through.
            ContentView()
                .tint(.brandOrange)
        }
    }
    
    private func startLogging() {
        #if DEBUG
        let volume = Level.verbose
        #else
        let volume = Level.debug
        #endif
        
        Bark.autoTagDisabled = false
        Bark.train(trainer: NSLogTrainer(minLevel: Level.verbose))
        Bark.train(trainer: SentryTrainer())
        Bark.v("Bark has been initilized for iOS successfully!")
    }
}
