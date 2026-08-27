import SwiftUI
import Shared
import DesignSystem

/// "Up Next" section: the nearest upcoming discussion across all of the
/// member's clubs. Flat section matching the rest of the Me screen — no card
/// fill/border. Read-only; attendance/RSVP is a separate ticket. Renders
/// nothing when there's no upcoming discussion.
struct UpNextSection: View {
    let upNext: Shared.UpNextItem?

    var body: some View {
        if let upNext {
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(String(localized: "up_next_eyebrow").uppercased())
                        .kluvsStyle(KluvsTheme.typography.eyebrow)
                        .foregroundColor(KluvsTheme.colors.accent)
                    Spacer()
                    Text(upNext.date)
                        .kluvsStyle(KluvsTheme.typography.caption)
                        .foregroundColor(KluvsTheme.colors.accent)
                }

                Text(upNext.title)
                    .kluvsStyle(KluvsTheme.typography.title.large, feature: true)
                    .foregroundColor(KluvsTheme.colors.content)

                Text([upNext.clubName, upNext.location].compactMap { $0 }.joined(separator: " — "))
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
            // Not `.padding()` — the outer VStack in `MeView` already applies horizontal padding
            // to every section; adding it again here doubled it, same bug as `ProfileSection`.
            .padding(.vertical, 16)
        }
    }
}

#Preview {
    UpNextSection(
        upNext: Shared.UpNextItem(
            title: "End-of-Year Check-in",
            clubName: "Showcase Kluv",
            location: "Online",
            date: "December 31, 2026"
        )
    )
}
