import SwiftUI
//
//  BooksTopBar.swift
//  iosApp
//
import DesignSystem

/// Single animated top bar for the Books tab — built on the shared `SearchTopAppBar`, which
/// generalizes the exact fade/unfurl animation this file originally hand-rolled (mirrors web's
/// header: the title fades out in place while a bordered search field scales in from the right,
/// landing where the search button used to be).
///
/// Two-row mode (`header` "Books" + `title` "Your Shelf") — mirrors Android's
/// `SearchTopAppBar(header = books, title = your_shelf_title)`.
struct BooksTopBar: View {
    @Binding var isSearchActive: Bool
    var isSearching: Bool = false
    @Binding var query: String

    @Environment(\.safeAreaInsets) private var safeAreaInsets

    var body: some View {
        SearchTopAppBar(
            header: String(localized: "books"),
            isSearchActive: $isSearchActive,
            searchQuery: $query,
            title: String(localized: "your_shelf_title"),
            isSearchLoading: isSearching,
            searchPlaceholder: String(localized: "search_books_hint")
        )
        .padding(.top, safeAreaInsets.top)
        .background(KluvsTheme.colors.background)
    }
}

#Preview {
    VStack {
        BooksTopBar(isSearchActive: .constant(false), query: .constant(""))
        BooksTopBar(isSearchActive: .constant(true), isSearching: true, query: .constant("Klara"))
    }
}
