import SwiftUI

/// Hollow tri-state RSVP option — decoupled from the app's `AttendanceStatus` domain type.
/// Mirrors Android's `AttendanceOption`.
public enum AttendanceOption {
    case yes, maybe, no
}

private let segments: [AttendanceOption] = [.yes, .maybe, .no]

/// RSVP control for a single discussion — mirrors Android's `AttendanceControl`. A 3-segment
/// icon pill (yes, maybe, no) plus a summary line.
///
/// Hollow — takes plain counts/selection instead of the app's `AttendanceRoster`/
/// `AttendanceStatus` domain types; callers translate at the call site via small private
/// extensions, same pattern Android's `ActiveSessionTab.kt` established.
///
/// - Parameters:
///   - counts: per-option response count, e.g. `[.yes: 4]` — missing keys render as 0.
///   - selected: the signed-in member's current RSVP, or `nil` if they haven't responded.
public struct AttendanceControl: View {
    let counts: [AttendanceOption: Int]
    let selected: AttendanceOption?
    let disabled: Bool
    let onSelect: (AttendanceOption) -> Void

    public init(
        counts: [AttendanceOption: Int],
        selected: AttendanceOption?,
        disabled: Bool,
        onSelect: @escaping (AttendanceOption) -> Void
    ) {
        self.counts = counts
        self.selected = selected
        self.disabled = disabled
        self.onSelect = onSelect
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 0) {
                ForEach(Array(segments.enumerated()), id: \.offset) { index, option in
                    AttendanceSegment(
                        option: option,
                        isSelected: selected == option,
                        disabled: disabled,
                        isFirst: index == 0,
                        onTap: { onSelect(option) }
                    )
                }
            }
            .clipShape(Capsule())
            .overlay(Capsule().strokeBorder(KluvsTheme.colors.divider, lineWidth: 1))
            .opacity(disabled ? 0.7 : 1)

            // Caption, not Eyebrow — plain metadata, matches design-system/docs/typography.md's
            // own "1 yes · 0 no · 0 maybe" example directly.
            Text("\(counts[.yes] ?? 0) yes · \(counts[.no] ?? 0) no · \(counts[.maybe] ?? 0) maybe")
                .kluvsStyle(KluvsTheme.typography.caption)
                .foregroundColor(KluvsTheme.colors.contentMuted)
        }
    }
}

private struct AttendanceSegment: View {
    let option: AttendanceOption
    let isSelected: Bool
    let disabled: Bool
    let isFirst: Bool
    let onTap: () -> Void

    private var background: Color {
        switch (isSelected, option) {
        case (true, .yes): KluvsTheme.colors.successSubtle
        case (true, .no): KluvsTheme.colors.dangerSubtle
        case (true, _): KluvsTheme.colors.cardAlt
        default: KluvsTheme.colors.card
        }
    }

    private var tint: Color {
        switch (isSelected, option) {
        case (true, .yes): KluvsTheme.colors.success
        case (true, .no): KluvsTheme.colors.danger
        case (true, _): KluvsTheme.colors.content
        default: KluvsTheme.colors.contentMuted
        }
    }

    private var iconType: IconType {
        switch option {
        case .yes: .check
        case .maybe: .help
        case .no: .close
        }
    }

    private var accessibilityDescription: String {
        switch option {
        case .yes: "RSVP yes"
        case .maybe: "RSVP maybe"
        case .no: "RSVP no"
        }
    }

    var body: some View {
        Button(action: onTap) {
            Icon(type: iconType, contentDescription: accessibilityDescription, tint: tint)
                .frame(width: 13, height: 13)
                .frame(width: 28, height: 28)
                .background(background)
                .overlay(alignment: .leading) {
                    if !isFirst {
                        Rectangle().fill(KluvsTheme.colors.divider).frame(width: 1)
                    }
                }
        }
        .disabled(disabled)
    }
}

#Preview {
    AttendanceControl(
        counts: [.yes: 1, .maybe: 1],
        selected: .yes,
        disabled: false,
        onSelect: { _ in }
    )
    .padding()
}
