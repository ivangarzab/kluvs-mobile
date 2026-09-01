import SwiftUI
import Shared
import DesignSystem

/// Edit Session sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`), not
/// as its own `View` wrapping a `.sheet`.
///
/// Only the due date is editable here — the session's book is intentionally not, matching
/// web's `EditSessionModal`: changing a book is treated as ending the current session and
/// starting a new one (a separate "Change Book" flow), not an edit. That flow is out of scope
/// here; this sheet simply doesn't offer a book field.
///
/// Due date is day-only — no time-of-day picker.
struct EditSessionFields: View {
    @Binding var hasDueDate: Bool
    @Binding var dueDate: Date

    var body: some View {
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
