import SwiftUI

private let contentFadeDuration = 0.15
private let searchUnfurlDuration = 0.2
private let titleRowHeight: CGFloat = 64

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

    private var barHeight: CGFloat {
        (isSearchActive || title == nil) ? topAppBarRowHeight : topAppBarRowHeight + titleRowHeight
    }

    public var body: some View {
        ZStack(alignment: .topLeading) {
            TopAppBar(header: header, title: title, onNavigateBack: onNavigateBack) {
                Group {
                    action()
                    IconButton(type: .search, contentDescription: "Search", action: { isSearchActive = true }, enabled: !isSearchActive)
                }
            }
            .opacity(isSearchActive ? 0 : 1)
            .animation(.easeInOut(duration: contentFadeDuration), value: isSearchActive)

            HStack {
                IconButton(type: .arrowBack, contentDescription: "Close search", action: { isSearchActive = false }, tint: KluvsTheme.colors.content)
                SearchField(value: $searchQuery, placeholder: searchPlaceholder, isLoading: isSearchLoading)
            }
            .padding(.horizontal, 8)
            .frame(height: topAppBarRowHeight)
            .scaleEffect(x: isSearchActive ? 1 : 0, y: 1, anchor: .trailing)
            .opacity(isSearchActive ? 1 : 0)
            .animation(.easeOut(duration: searchUnfurlDuration), value: isSearchActive)
        }
        .frame(height: barHeight)
        .animation(.easeOut(duration: searchUnfurlDuration), value: barHeight)
        .clipped()
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
