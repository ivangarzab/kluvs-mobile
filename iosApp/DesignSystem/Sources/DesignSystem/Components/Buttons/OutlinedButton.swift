import SwiftUI

/// Supporting/secondary action, "muted" emphasis (design-system "Secondary / Outlined", grey
/// variant — see design-system/docs/buttons.md), e.g. "Join this Read" / "Opt out" / "Update"
/// progress. Use `SecondaryButton` for the copper/active variant of this same role. Mirrors
/// Android's `OutlinedButton`.
public struct OutlinedButton: View {
    let text: String
    let action: () -> Void
    var enabled: Bool

    public init(text: String, action: @escaping () -> Void, enabled: Bool = true) {
        self.text = text
        self.action = action
        self.enabled = enabled
    }

    public var body: some View {
        Button(action: action) {
            Text(text)
                .kluvsStyle(KluvsTheme.typography.label)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .padding(.vertical, 8)
                .padding(.horizontal, 8)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(KluvsTheme.colors.divider, lineWidth: 1)
                )
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    VStack(spacing: 12) {
        OutlinedButton(text: "Join this Read", action: {})
        OutlinedButton(text: "Join this Read", action: {}, enabled: false)
    }
    .padding()
}
