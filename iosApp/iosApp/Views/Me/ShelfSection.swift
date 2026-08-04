import SwiftUI
import Shared
import DesignSystem

/// "On Your Shelf" section on the Me screen: eyebrow header + book count
/// caption, then one `ShelfRow` per active-session book. Mirrors web's
/// ProfilePage shelf list.
struct ShelfSection: View {
    let shelf: [Shared.ShelfItem]
    let onUpdateProgress: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .bottom) {
                Text(String(localized: "on_your_shelf").uppercased())
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                Spacer()
                if !shelf.isEmpty {
                    Text(String(format: NSLocalizedString("books_in_progress_x", comment: ""), shelf.count))
                        .kluvsStyle(KluvsTheme.typography.title.small, feature: true)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                }
            }

            VStack(alignment: .leading, spacing: 20) {
                ForEach(shelf, id: \.sessionId) { item in
                    ShelfRow(item: item, onUpdateProgress: onUpdateProgress)
                }
            }
        }
        // Not `.padding()` — the outer VStack in `MeView` already applies horizontal padding to
        // every section; adding it again here doubled it, same bug as `ProfileSection`.
        .padding(.vertical, 16)
    }
}

#Preview {
    ShelfSection(
        shelf: [
            Shared.ShelfItem(
                sessionId: "s0", bookId: "b0", bookTitle: "How AI Thinks", bookAuthor: "Nigel Toon",
                bookCoverUrl: nil, bookPageCount: 328, clubId: "c0", clubName: "Showcase Kluv",
                nextDiscussionDate: "December 31, 2026", ownProgress: nil
            )
        ],
        onUpdateProgress: { _ in }
    )
}
