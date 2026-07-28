import SwiftUI
import DesignSystem

/// Root-mode top bar for the Me tab — mirrors `BooksTopBar`'s self-owned layout
/// (title left, one trailing utility icon right). Reading Log, Settings, and
/// Sign Out are all exposed via the trailing kebab menu, matching Android's
/// `MeScreen` ActionMenu.
struct MeTopBar: View {
    var onReadingLogClick: () -> Void = {}
    var onSettingsClick: () -> Void = {}
    var onSignOutClick: () -> Void = {}

    @Environment(\.safeAreaInsets) private var safeAreaInsets

    var body: some View {
        HStack {
            Text(String(localized: "tab_me"))
                .font(.title2)
                .fontWeight(.bold)
            Spacer()
            ActionMenu(items: [
                ActionMenuItem(label: String(localized: "reading_log"), action: onReadingLogClick),
                ActionMenuItem(label: String(localized: "button_settings"), action: onSettingsClick),
                ActionMenuItem(label: String(localized: "sign_out"), action: onSignOutClick, isDestructive: true)
            ])
        }
        .padding(.horizontal, 16)
        .frame(height: 56)
        .padding(.top, safeAreaInsets.top)
        .background(Color.kluvsBackground)
    }
}

#Preview {
    MeTopBar()
}
