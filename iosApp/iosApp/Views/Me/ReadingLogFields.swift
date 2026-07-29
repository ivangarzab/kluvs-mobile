import SwiftUI
import Shared
import DesignSystem

/// Reading Log sheet body — presented via `.kluvsBottomSheet` at the call site (`MeView`), not as
/// its own `View` wrapping a `.sheet`. No footer — read-only, matching web's `ReadingLogModal`
/// (no Save/Cancel row, closes via the header's × / here, the scrim tap or drag-to-dismiss).
struct ReadingLogFields: View {
    let log: Shared.ReadingLog?
    let isLoading: Bool

    var body: some View {
        if isLoading {
            HStack {
                Spacer()
                LoadingSpinner()
                Spacer()
            }
            .padding(.vertical, 32)
        } else {
            VStack(alignment: .leading, spacing: 16) {
                ReadingLogGroup(title: String(localized: "shelf_currently_reading"), entries: log?.active ?? [])
                Divider()
                ReadingLogGroup(title: String(localized: "shelf_read"), entries: log?.finished ?? [])
            }
        }
    }
}

private struct ReadingLogGroup: View {
    let title: String
    let entries: [Shared.ReadingLogEntry]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title.uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(KluvsTheme.colors.contentMuted)

            if entries.isEmpty {
                Text(String(localized: "nothing_here_yet"))
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .italic()
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            } else {
                VStack(alignment: .leading, spacing: 12) {
                    ForEach(entries, id: \.sessionId) { entry in
                        ReadingLogRow(entry: entry)
                    }
                }
            }
        }
    }
}

private struct ReadingLogRow: View {
    let entry: Shared.ReadingLogEntry

    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            BookCoverImage(imageUrl: entry.book?.imageUrl, width: 40)

            VStack(alignment: .leading, spacing: 2) {
                Text(entry.book?.title ?? "")
                    .kluvsStyle(KluvsTheme.typography.title.small, feature: true)
                    .foregroundColor(KluvsTheme.colors.content)
                    .lineLimit(1)
                Text(entry.book?.author ?? "")
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                    .lineLimit(1)
                Text((entry.club?.name ?? "").uppercased())
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
        }
    }
}

#Preview {
    ReadingLogFields(log: nil, isLoading: false)
        .padding()
}
