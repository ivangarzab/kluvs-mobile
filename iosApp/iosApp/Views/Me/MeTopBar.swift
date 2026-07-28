import SwiftUI
import DesignSystem

/// Root-mode top bar for the Me tab — built on the shared `TopAppBar` (single-row,
/// header-only mode), matching Android's `header = "Me"` choice rather than a bold
/// page title. Reading Log, Settings, and Sign Out are all exposed via the trailing
/// kebab menu, matching Android's `MeScreen` ActionMenu.
struct MeTopBar: View {
    var onReadingLogClick: () -> Void = {}
    var onSettingsClick: () -> Void = {}
    var onSignOutClick: () -> Void = {}

    @Environment(\.safeAreaInsets) private var safeAreaInsets

    var body: some View {
        TopAppBar(header: String(localized: "tab_me")) {
            ActionMenu(items: [
                ActionMenuItem(label: String(localized: "reading_log"), action: onReadingLogClick),
                ActionMenuItem(label: String(localized: "button_settings"), action: onSettingsClick),
                ActionMenuItem(label: String(localized: "sign_out"), action: onSignOutClick, isDestructive: true)
            ])
        }
        .padding(.top, safeAreaInsets.top)
        .background(Color.kluvsBackground)
    }
}

#Preview {
    MeTopBar()
}
