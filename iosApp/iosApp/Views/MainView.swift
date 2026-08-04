import SwiftUI
import DesignSystem

struct MainView: View {
    let userId: String
    var initialClubId: String? = nil
    var onNavigateToJoin: () -> Void = {}
    // Tab order is Me(0) - Clubs(1) - Books(2), but landing on an auto-joined club (deep link /
    // post-signup join) still needs to open straight into the Clubs tab, not the default Me tab.
    @State private var selectedTab: Int

    init(userId: String, initialClubId: String? = nil, onNavigateToJoin: @escaping () -> Void = {}) {
        self.userId = userId
        self.initialClubId = initialClubId
        self.onNavigateToJoin = onNavigateToJoin
        self._selectedTab = State(initialValue: initialClubId != nil ? 1 : 0)
    }

    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                // Clubs, Books, and Me each own their own top bar/heading UI (ClubsListView's
                // masthead, BooksTopBar, MeTopBar), so the shared Material-style TopAppBar
                // is unused for all tabs now.

                // Content area
                Group {
                    if selectedTab == 0 {
                        MeView(userId: userId, onNavigateToClubs: { selectedTab = 1 })
                    } else if selectedTab == 1 {
                        ClubsView(userId: userId, initialClubId: initialClubId, onNavigateToJoin: onNavigateToJoin)
                    } else {
                        BooksView()
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.kluvsBackground)

                MaterialBottomNavBar(selectedTab: $selectedTab)
            }
            .background(Color.kluvsBackground)
            .ignoresSafeArea(edges: .bottom)
            .onChange(of: initialClubId) { _, newValue in
                if newValue != nil { selectedTab = 1 }
            }
        }
    }
}

// Helper to access safe area insets
private struct SafeAreaInsetsKey: EnvironmentKey {
    static var defaultValue: EdgeInsets {
        EdgeInsets()
    }
}

extension EnvironmentValues {
    var safeAreaInsets: EdgeInsets {
        self[SafeAreaInsetsKey.self]
    }
}

#Preview {
    MainView(userId: "1")
}
