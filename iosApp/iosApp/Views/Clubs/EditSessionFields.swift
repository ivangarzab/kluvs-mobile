import SwiftUI
import Shared
import DesignSystem

/// Edit Session sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`), not
/// as its own `View` wrapping a `.sheet`. Same real gap as `CreateSessionFields`: manual Title/
/// Author entry instead of web's real book search, kept as-is (not a shell-pass concern) and
/// flagged there rather than repeated here.
struct EditSessionFields: View {
    @Binding var bookTitle: String
    @Binding var bookAuthor: String
    @Binding var hasDueDate: Bool
    @Binding var dueDate: Date

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 12) {
                Text("BOOK")
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                InputField(label: "Title", value: $bookTitle)
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
