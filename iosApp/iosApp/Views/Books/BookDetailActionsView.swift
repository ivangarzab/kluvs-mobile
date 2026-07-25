//
//  BookDetailActionsView.swift
//  iosApp
//
import SwiftUI
import Shared
import DesignSystem

private let assignableShelfStatuses: [Shared.ShelfStatus] = [.currentlyReading, .read, .wantToRead, .notFinished]

/// Shelf status selector + like toggle for the book detail screen, styled to match web's
/// `LikePill`/`ShelfPill`: fully rounded outline pills, copper border when active, neutral
/// otherwise. Hidden entirely for unregistered books, mirroring [BookCard]'s guard.
struct BookDetailActionsView: View {
    let isRegistered: Bool
    let shelfStatus: Shared.ShelfStatus?
    let isLiked: Bool
    let isMutationInProgress: Bool
    let onShelfChange: (Shared.ShelfStatus?) -> Void
    let onToggleLike: () -> Void

    var body: some View {
        if isRegistered {
            HStack(spacing: 10) {
                TogglePill(
                    checked: isLiked,
                    onToggle: onToggleLike,
                    iconChecked: .favorite,
                    iconUnchecked: .favoriteOutline,
                    contentDescription: String(localized: isLiked ? "unlike_book" : "like_book"),
                    enabled: !isMutationInProgress
                )
                Dropdown(
                    options: assignableShelfStatuses,
                    selected: shelfStatus,
                    onSelect: onShelfChange,
                    label: shelfLabel,
                    placeholder: String(localized: "shelf_add_to_shelf"),
                    clearLabel: String(localized: "shelf_none"),
                    enabled: !isMutationInProgress
                )
            }
        }
    }
}

private func shelfLabel(_ status: Shared.ShelfStatus) -> String {
    switch status {
    case .currentlyReading: return String(localized: "shelf_currently_reading")
    case .read: return String(localized: "shelf_read")
    case .wantToRead: return String(localized: "shelf_want_to_read")
    case .notFinished: return String(localized: "shelf_not_finished")
    default: return ""
    }
}

#Preview {
    BookDetailActionsView(
        isRegistered: true,
        shelfStatus: .currentlyReading,
        isLiked: true,
        isMutationInProgress: false,
        onShelfChange: { _ in },
        onToggleLike: {}
    )
    .padding()
}
