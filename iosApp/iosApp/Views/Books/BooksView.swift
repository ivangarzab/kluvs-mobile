//
//  BooksView.swift
//  iosApp
//
import SwiftUI
import Shared
import DesignSystem

private let shelfSections: [Shared.ShelfStatus] = [.currentlyReading, .read, .wantToRead, .notFinished]
private let searchDebounceNanoseconds: UInt64 = 400_000_000

/// Carries the tapped `Book` value itself through the `NavigationPath`, rather than pushing
/// just its id and having the destination closure read a separately-set `@State` — that split
/// is a real race: the id push and the state mutation are two separate updates, and
/// `NavigationStack` can resolve the destination from a snapshot that predates the second one,
/// most visibly on the very first push in a session (shows a blank destination with no top bar
/// until a second tap "catches up"). `Shared.Book` isn't `Hashable` on the Swift side, so this
/// wraps it and forwards equality/hash to its `id`.
private struct BookRoute: Hashable {
    let book: Shared.Book
    static func == (lhs: BookRoute, rhs: BookRoute) -> Bool { lhs.book.id == rhs.book.id }
    func hash(into hasher: inout Hasher) { hasher.combine(book.id) }
}

struct BooksView: View {
    @StateObject private var viewModel = BooksViewModelWrapper()
    @State private var isSearchActive = false
    @State private var searchTask: Task<Void, Never>?
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 0) {
                BooksTopBar(
                    isSearchActive: Binding(
                        get: { isSearchActive },
                        set: { newValue in
                            isSearchActive = newValue
                            // Closing search also clears the query — matches the old
                            // onBackClick behavior this binding replaces.
                            if !newValue { viewModel.onQueryChange("") }
                        }
                    ),
                    isSearching: viewModel.isSearching,
                    query: Binding(
                        get: { viewModel.query },
                        set: { viewModel.onQueryChange($0) }
                    )
                )

                if viewModel.isMutationInProgress {
                    ProgressView()
                        .progressViewStyle(LinearProgressViewStyle())
                        .tint(.brandOrange)
                }

                if isSearchActive {
                    SearchContent(viewModel: viewModel, onBookTap: { book in
                        path.append(BookRoute(book: book))
                    })
                } else {
                    ShelfContent(viewModel: viewModel, onBookTap: { book in
                        path.append(BookRoute(book: book))
                    })
                }
            }
            .navigationDestination(for: BookRoute.self) { route in
                let book = route.book
                let shelfEntry = viewModel.shelfEntries.first { $0.book.id == book.id }
                BookDetailView(
                    book: book,
                    initialShelfStatus: shelfEntry?.shelf,
                    initialShelfSource: shelfEntry?.source,
                    onNavigateToBook: { nextBook in
                        path.append(BookRoute(book: nextBook))
                    }
                )
            }
        }
        .onAppear { viewModel.loadShelf() }
        .onChange(of: viewModel.query) { _, query in
            searchTask?.cancel()
            guard isSearchActive else { return }
            searchTask = Task {
                try? await Task.sleep(nanoseconds: searchDebounceNanoseconds)
                guard !Task.isCancelled else { return }
                viewModel.search(query)
            }
        }
        .kluvsConfirmationDialog(
            isPresented: Binding(
                get: { viewModel.operationError != nil },
                set: { _ in }
            ),
            title: "Result",
            message: viewModel.operationError ?? "",
            confirmLabel: "OK",
            dismissLabel: nil,
            onDismiss: { viewModel.onConsumeOperationError() },
            onConfirm: { viewModel.onConsumeOperationError() }
        )
    }
}

// MARK: - Shelf

private struct ShelfContent: View {
    @ObservedObject var viewModel: BooksViewModelWrapper
    let onBookTap: (Shared.Book) -> Void

    var body: some View {
        switch viewModel.shelfScreenState {
        case .loading:
            BooksShelfSkeleton()
        case .error(let message):
            ErrorView(message: message, onRetry: { viewModel.loadShelf() })
        case .empty:
            VStack {
                Spacer()
                Text(String(localized: "no_books_shelved"))
                    .kluvsStyle(KluvsTheme.typography.body.large)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                Spacer()
            }
        case .content:
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    ForEach(shelfSections, id: \.ordinal) { section in
                        let entries = viewModel.shelfEntries.filter { $0.shelf == section }
                        if !entries.isEmpty {
                            ShelfSectionView(section: section, entries: entries, onBookTap: onBookTap)
                        }
                    }
                }
                .padding(.bottom, 16)
            }
            .kluvsPullToRefresh(isRefreshing: viewModel.isRefreshingShelf) {
                viewModel.loadShelf(forceRefresh: true)
            }
        }
    }
}

private struct ShelfSectionView: View {
    let section: Shared.ShelfStatus
    let entries: [Shared.ShelfEntry]
    let onBookTap: (Shared.Book) -> Void

    @Environment(\.colorScheme) private var colorScheme

    private var eyebrowColor: Color { colorScheme == .dark ? Color(hex: 0xB0B0B0) : .foregroundLightSecondary }
    private var countColor: Color { colorScheme == .dark ? .foregroundWarmTertiary : .foregroundLightTertiary }

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(alignment: .center, spacing: 8) {
                // Eyebrow — design-system component.eyebrow: IBM Plex Sans 11px/500, uppercase, 0.14em tracking
                Text(sectionLabel(section).uppercased())
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(eyebrowColor)
                Text("\(entries.count)")
                    .font(.plexSans(size: 10))
                    .foregroundColor(countColor)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 4)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(entries, id: \.book.id) { entry in
                        BookCard(
                            book: entry.book,
                            shelfSource: entry.source,
                            onTap: { onBookTap(entry.book) }
                        )
                    }
                }
                .padding(.horizontal, 16)
            }
        }
    }
}

private func sectionLabel(_ status: Shared.ShelfStatus) -> String {
    switch status {
    case .currentlyReading: return String(localized: "shelf_currently_reading")
    case .read: return String(localized: "shelf_read")
    case .wantToRead: return String(localized: "shelf_want_to_read")
    case .notFinished: return String(localized: "shelf_not_finished")
    default: return ""
    }
}

// MARK: - Search

private struct SearchContent: View {
    @ObservedObject var viewModel: BooksViewModelWrapper
    let onBookTap: (Shared.Book) -> Void

    private let gridColumns = [GridItem(.adaptive(minimum: 120), spacing: 12)]

    var body: some View {
        Group {
            if viewModel.query.trimmingCharacters(in: .whitespaces).isEmpty {
                SearchEmptyState(
                    heading: String(localized: "start_typing"),
                    bodyText: String(localized: "start_typing_hint")
                )
            } else if viewModel.isSearching && viewModel.searchResults.isEmpty {
                LoadingView()
            } else if let error = viewModel.searchError {
                ErrorView(message: error, onRetry: { viewModel.search(viewModel.query) })
            } else if viewModel.searchResults.isEmpty {
                SearchEmptyState(
                    heading: String(localized: "no_matches"),
                    bodyText: String(format: String(localized: "no_books_found_for_x"), viewModel.query)
                )
            } else {
                ScrollView {
                    LazyVGrid(columns: gridColumns, spacing: 12) {
                        ForEach(viewModel.searchResults, id: \.id) { book in
                            BookCard(
                                book: book,
                                onTap: { onBookTap(book) }
                            )
                        }
                    }
                    .padding(16)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct SearchEmptyState: View {
    let heading: String
    let bodyText: String

    var body: some View {
        VStack {
            Spacer()
            VStack(spacing: 16) {
                StackedCoverPlaceholder()
                VStack(spacing: 4) {
                    Text(heading)
                        .font(.ebGaramondMediumItalic(size: 28))
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                    Text(bodyText)
                        .kluvsStyle(KluvsTheme.typography.body.medium)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 32)
                }
            }
            Spacer()
        }
    }
}

#Preview {
    BooksView()
}
