import SwiftUI

/// Single row's height, shared with `SearchTopAppBar` so the collapse-to-search animation
/// targets an exact, known value rather than measuring.
public let topAppBarRowHeight: CGFloat = 56

/// Editorial page header — an eyebrow-style `header` label (e.g. "Profile", "Club", "Library")
/// always shown in the top row alongside the optional back button and `action`, plus an optional
/// big serif `title` (the actual name/value) in a second row underneath. Two modalities, not two
/// components: pass `title` for the full two-row version; omit it and the bar is just the single
/// header row. See design-system/docs/navigation.md. Mirrors Android's `TopAppBar`.
///
/// For the animated collapse-into-search variant of this same bar, see `SearchTopAppBar`.
public struct TopAppBar<Action: View>: View {
    let header: String
    var title: String?
    var onNavigateBack: (() -> Void)?
    var action: () -> Action

    public init(
        header: String,
        title: String? = nil,
        onNavigateBack: (() -> Void)? = nil,
        @ViewBuilder action: @escaping () -> Action = { EmptyView() }
    ) {
        self.header = header
        self.title = title
        self.onNavigateBack = onNavigateBack
        self.action = action
    }

    // Matches Android's TopAppBar exactly: the header label gets no extra inline padding only
    // in the single-row, has-a-back-button case (it sits flush after the button); every other
    // case — with a title, or with no back button — adds 8pt so it doesn't hug the row edge.
    private var headerInlinePadding: CGFloat {
        (title == nil && onNavigateBack != nil) ? 0 : 8
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 4) {
                if let onNavigateBack {
                    IconButton(type: .arrowBack, contentDescription: "Back", action: onNavigateBack)
                }
                Text(header.uppercased())
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                    .padding(.horizontal, headerInlinePadding)
                    .frame(maxWidth: .infinity, alignment: .leading)
                action()
            }
            .padding(.horizontal, 8)
            .frame(height: topAppBarRowHeight)
            .foregroundColor(KluvsTheme.colors.content)

            if let title {
                Text(title)
                    .kluvsStyle(KluvsTheme.typography.headline.small)
                    .foregroundColor(KluvsTheme.colors.content)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 8)
                    .padding(.bottom, 16)
            }
        }
    }
}

#Preview("Full") {
    VStack(spacing: 16) {
        TopAppBar(header: "Club", title: "The Great Gatsby Book Club") {
            IconButton(type: .help, contentDescription: "More", action: {})
        }
        TopAppBar(header: "Club", title: "The Great Gatsby Book Club", onNavigateBack: {}) {
            IconButton(type: .moreVert, contentDescription: "More", action: {})
        }
    }
    .background(KluvsTheme.colors.background)
}

#Preview("Single row") {
    VStack(spacing: 16) {
        TopAppBar(header: "Settings", onNavigateBack: {})
        TopAppBar(header: "Settings")
    }
    .background(KluvsTheme.colors.background)
}
