import SwiftUI
import DesignSystem

/// Discussion sheet body (create + edit) — presented via `.kluvsBottomSheet` at the call site
/// (`ClubsView`), not as its own `View` wrapping a `.sheet`. Checked kluvs-frontend's
/// DiscussionModal.tsx as source of truth: Location is optional there (only Title/Date/Time are
/// required) — this sheet previously required Location too, a real mismatch, now fixed to match
/// web. Also adds the "READING SESSION" read-only info box web shows (the active session's book
/// title), which this sheet never had.
///
/// Web splits Date and Time into two separate fields; kept as one combined `DatePicker` here since
/// that's the native iOS idiom for date+time entry, not a parity gap.
struct DiscussionFields: View {
    @Binding var title: String
    @Binding var location: String
    @Binding var selectedDate: Date
    let bookTitle: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            if let bookTitle {
                VStack(alignment: .leading, spacing: 4) {
                    Text("READING SESSION")
                        .kluvsStyle(KluvsTheme.typography.eyebrow)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                    Text(bookTitle)
                        .kluvsStyle(KluvsTheme.typography.body.medium)
                        .foregroundColor(KluvsTheme.colors.content)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(KluvsTheme.colors.cardAlt)
                .overlay(RoundedRectangle(cornerRadius: 8).strokeBorder(KluvsTheme.colors.divider, lineWidth: 1))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }

            VStack(alignment: .leading, spacing: 12) {
                InputField(label: "Title", value: $title)
                InputField(label: "Location (optional)", value: $location)
            }

            VStack(alignment: .leading, spacing: 8) {
                Text("DATE & TIME")
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                DatePicker(
                    "Date",
                    selection: $selectedDate,
                    displayedComponents: [.date, .hourAndMinute]
                )
                .datePickerStyle(.compact)
                .labelsHidden()
            }
        }
    }
}
