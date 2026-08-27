import SwiftUI
import Shared
import DesignSystem

/// Create Session sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`),
/// not as its own `View` wrapping a `.sheet`.
///
/// NOTE — real gap, not just a shell change: web's `NewSessionModal` uses `BookSearchInput`, a real
/// Google Books search that selects an existing book by ID. This sheet still has the user type a
/// free-text Title/Author, creating a placeholder `Shared.Book(id: "", ...)` with no ISBN, cover,
/// or page count. That's a missing feature (book search wired into session creation), not
/// something a shell-restyling pass should attempt — left as manual entry, flagged here rather than
/// silently kept as-is. Web's "all members reading" participation toggle is the same story: no
/// `allReading` param exists on the shared `onCreateSession` call today.
struct CreateSessionFields: View {
    @Binding var bookTitle: String
    @Binding var bookAuthor: String
    @Binding var hasDueDate: Bool
    @Binding var dueDate: Date

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 12) {
                InputField(label: "Book Title", value: $bookTitle)
                InputField(label: "Author", value: $bookAuthor)
            }

            VStack(alignment: .leading, spacing: 12) {
                Toggle("Set a due date", isOn: $hasDueDate)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.content)
                    .tint(KluvsTheme.colors.accent)
                if hasDueDate {
                    DatePicker(
                        "Due Date",
                        selection: $dueDate,
                        displayedComponents: [.date, .hourAndMinute]
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
