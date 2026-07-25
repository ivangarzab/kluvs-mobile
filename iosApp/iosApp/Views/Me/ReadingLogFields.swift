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
                ProgressView()
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
                .font(.kluvsEyebrow)
                .foregroundColor(.secondary)

            if entries.isEmpty {
                Text(String(localized: "nothing_here_yet"))
                    .font(.ebGaramondItalic(size: 15))
                    .foregroundColor(.secondary)
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
                    .font(.ebGaramondItalic(size: 16))
                    .lineLimit(1)
                Text(entry.book?.author ?? "")
                    .font(.kluvsBody)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
                Text((entry.club?.name ?? "").uppercased())
                    .font(.plexSansMedium(size: 11))
                    .foregroundColor(.secondary)
            }
        }
    }
}

#Preview {
    ReadingLogFields(log: nil, isLoading: false)
        .padding()
}
