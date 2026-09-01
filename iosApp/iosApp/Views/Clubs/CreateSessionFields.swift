import SwiftUI
import Shared
import DesignSystem

/// Create Session sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`),
/// not as its own `View` wrapping a `.sheet`.
///
/// Book selection is a real search-and-select field (`BookSearchField`) backed by
/// `ClubDetailsViewModelWrapper`'s book-search state — selecting a result registers it
/// server-side to obtain a real `bookId`, matching web's `NewSessionModal`/`BookSearchInput`.
///
/// Due date is day-only — no time-of-day picker. Web's "all members reading" participation
/// toggle isn't wired up here either: no `allReading` param exists on the shared
/// `onCreateSession` call today.
struct CreateSessionFields: View {
    @Binding var bookSearchQuery: String
    let bookSearchResults: [Shared.Book]
    let selectedBook: Shared.Book?
    let isSearchingBooks: Bool
    let isRegisteringBook: Bool
    let bookSearchError: String?
    let onSearchBooks: (String) -> Void
    let onSelectBook: (Shared.Book) -> Void
    let onClearBook: () -> Void
    @Binding var hasDueDate: Bool
    @Binding var dueDate: Date

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            BookSearchField(
                query: $bookSearchQuery,
                results: bookSearchResults,
                selectedBook: selectedBook,
                isSearching: isSearchingBooks,
                isRegistering: isRegisteringBook,
                error: bookSearchError,
                onSearch: onSearchBooks,
                onSelect: onSelectBook,
                onClear: onClearBook
            )

            VStack(alignment: .leading, spacing: 12) {
                Toggle("Set a due date", isOn: $hasDueDate)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.content)
                    .tint(KluvsTheme.colors.accent)
                if hasDueDate {
                    DatePicker(
                        "Due Date",
                        selection: $dueDate,
                        displayedComponents: [.date]
                    )
                    .datePickerStyle(.compact)
                }
                Text("When should members finish reading this book?")
                    .kluvsStyle(KluvsTheme.typography.caption)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
        }
    }
}
