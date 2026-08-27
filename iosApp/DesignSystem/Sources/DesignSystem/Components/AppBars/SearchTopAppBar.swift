import SwiftUI

private let searchUnfurlDuration = 0.2

/// `TopAppBar` with search baked in — a search action (alongside `action`) unfurls a
/// `SearchField` in from the right (scale-x, matching the original hand-rolled `BooksTopBar` this
/// generalizes), fading the header/title content out and shrinking the bar down to a single row
/// height for the duration of the search, regardless of whether `title` would otherwise put it in
/// two-row mode. Mirrors Android's `SearchTopAppBar`.
///
/// Caller owns `isSearchActive`/`searchQuery` (same convention as the screen this pattern is
/// lifted from) — this component only renders the transition, it doesn't own search state or
/// perform the search itself.
public struct SearchTopAppBar<Action: View>: View {
    let header: String
    @Binding var isSearchActive: Bool
    @Binding var searchQuery: String
    var title: String?
    var onNavigateBack: (() -> Void)?
    var action: () -> Action
    var isSearchLoading: Bool
    var searchPlaceholder: String

    @FocusState private var isSearchFieldFocused: Bool

    public init(
        header: String,
        isSearchActive: Binding<Bool>,
        searchQuery: Binding<String>,
        title: String? = nil,
        onNavigateBack: (() -> Void)? = nil,
        isSearchLoading: Bool = false,
        searchPlaceholder: String = "Search",
        @ViewBuilder action: @escaping () -> Action = { EmptyView() }
    ) {
        self.header = header
        self._isSearchActive = isSearchActive
        self._searchQuery = searchQuery
        self.title = title
        self.onNavigateBack = onNavigateBack
        self.isSearchLoading = isSearchLoading
        self.searchPlaceholder = searchPlaceholder
        self.action = action
    }

    // Stacking several independent `.animation(_:value:)` modifiers (one per property, plus one
    // on the outer frame height) let SwiftUI run them as separate, uncoordinated transactions —
    // visibly, the bar's height collapse would outrun the content fade/unfurl and the whole bar
    // would momentarily rocket up off the top of the screen. Driving every animatable change
    // from a single `withAnimation` at the two toggle sites keeps them in one transaction instead.
    private func setSearchActive(_ active: Bool) {
        withAnimation(.easeInOut(duration: searchUnfurlDuration)) {
            isSearchActive = active
        }
    }

    public var body: some View {
        // Previously a ZStack holding both rows permanently, with the container's own height
        // driven by a hand-computed `barHeight` (row height + a *guessed* constant for the
        // title row's real height). That guess never quite matched the title Text's actual
        // measured height, and the mismatch is what was making the whole bar visibly hop by a
        // few points — worst right at the start of the collapse, which reads as the bar
        // "jumping up toward the status bar" the moment search opens. Making the two rows
        // mutually exclusive instead means the container's height always comes from the real,
        // currently-visible content — nothing to guess, nothing to mismatch.
        Group {
            if isSearchActive {
                HStack {
                    IconButton(type: .arrowBack, contentDescription: "Close search", action: { setSearchActive(false) }, tint: KluvsTheme.colors.content)
                    SearchField(value: $searchQuery, placeholder: searchPlaceholder, isLoading: isSearchLoading, focus: $isSearchFieldFocused)
                }
                .padding(.horizontal, 8)
                .frame(height: topAppBarRowHeight)
                // The unfurl-from-the-right effect the old scaleEffect(x:) gave the search row —
                // recreated as a per-branch transition instead of a manually driven property, so
                // it rides along with the mutually-exclusive if/else's own animation rather than
                // needing its own hand-computed geometry.
                .transition(.scale(scale: 0.01, anchor: .trailing).combined(with: .opacity))
            } else {
                TopAppBar(header: header, title: title, onNavigateBack: onNavigateBack) {
                    Group {
                        action()
                        OutlinedIconButton(type: .search, contentDescription: "Search", action: { setSearchActive(true) })
                    }
                }
                .transition(.opacity)
            }
        }
        // Mirrors Android's LaunchedEffect(isSearchActive): opening search focuses the field
        // (and shows the keyboard); closing it explicitly resigns focus, since SwiftUI won't
        // dismiss the keyboard on its own just because the field faded out.
        .onChange(of: isSearchActive) { _, active in
            isSearchFieldFocused = active
        }
    }
}

#Preview("Idle") {
    StatefulSearchTopAppBarPreview(isSearchActive: false, query: "") { isSearchActive, query in
        SearchTopAppBar(
            header: "Library",
            isSearchActive: isSearchActive,
            searchQuery: query,
            title: "My Shelf",
            onNavigateBack: {},
            searchPlaceholder: "Search books"
        )
    }
    .background(KluvsTheme.colors.background)
}

#Preview("Active") {
    StatefulSearchTopAppBarPreview(isSearchActive: true, query: "Kluvs") { isSearchActive, query in
        SearchTopAppBar(
            header: "Library",
            isSearchActive: isSearchActive,
            searchQuery: query,
            title: "My Shelf",
            onNavigateBack: {},
            searchPlaceholder: "Search books"
        )
    }
    .background(KluvsTheme.colors.background)
}

private struct StatefulSearchTopAppBarPreview<Content: View>: View {
    @State private var isSearchActive: Bool
    @State private var query: String
    let content: (Binding<Bool>, Binding<String>) -> Content

    init(isSearchActive: Bool, query: String, @ViewBuilder content: @escaping (Binding<Bool>, Binding<String>) -> Content) {
        self._isSearchActive = State(initialValue: isSearchActive)
        self._query = State(initialValue: query)
        self.content = content
    }

    var body: some View {
        content($isSearchActive, $query)
    }
}
