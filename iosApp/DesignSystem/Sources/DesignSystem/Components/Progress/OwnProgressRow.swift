import SwiftUI

/// A member's progress on a session book: thin bar, status label, and the entry point to the
/// progress edit sheet. Hollow — takes plain values instead of the app's `OwnProgressInfo` domain
/// type; callers destructure their own model before calling this. Mirrors Android's
/// `OwnProgressRow`.
///
/// - Parameters:
///   - percent: 0-100, or `nil` if progress hasn't started yet — drives both the progress bar and
///     the "Update" vs "Track Progress" button label.
///   - statusLabel: e.g. "42 of 169 pages" — `nil` renders "Not started".
///   - leftLabel: e.g. "Your progress", "Next · Thu, Dec 31", or a formatted "3 of 5 discussions"
///     string — callers own the exact copy.
///   - leftLabelEmphasized: italicizes `leftLabel` via `kluvsStyle`'s `feature` flag
///     (Caption+feature) — the Overview tab's discussion-count line uses this; plain shelf-row
///     labels don't.
public struct OwnProgressRow: View {
    let percent: Int?
    let statusLabel: String?
    let onUpdateProgress: () -> Void
    var leftLabel: String
    var leftLabelEmphasized: Bool

    public init(
        percent: Int?,
        statusLabel: String?,
        onUpdateProgress: @escaping () -> Void,
        leftLabel: String = "Your progress",
        leftLabelEmphasized: Bool = false
    ) {
        self.percent = percent
        self.statusLabel = statusLabel
        self.onUpdateProgress = onUpdateProgress
        self.leftLabel = leftLabel
        self.leftLabelEmphasized = leftLabelEmphasized
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 12) {
                ProgressBar(percent: percent ?? 0)
                OutlinedButton(text: percent != nil ? "Update" : "Track Progress", action: onUpdateProgress)
            }
            HStack {
                Text(leftLabel)
                    .kluvsStyle(KluvsTheme.typography.caption, feature: leftLabelEmphasized)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                Spacer()
                Text(statusLabel ?? "Not started")
                    .kluvsStyle(KluvsTheme.typography.caption)
                    .foregroundColor(KluvsTheme.colors.accent)
            }
        }
    }
}

#Preview {
    VStack(spacing: 24) {
        OwnProgressRow(percent: 25, statusLabel: "42 of 169 pages", onUpdateProgress: {}, leftLabel: "Next · Thu, Dec 31")
        OwnProgressRow(percent: 60, statusLabel: "42 of 169 pages", onUpdateProgress: {}, leftLabel: "3 of 5 discussions", leftLabelEmphasized: true)
    }
    .padding()
}
