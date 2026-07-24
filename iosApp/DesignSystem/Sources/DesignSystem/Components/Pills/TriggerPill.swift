import SwiftUI

/// One-shot action rendered as a tiny outlined chip (design-system "Pill Button" — see
/// design-system/docs/buttons.md), e.g. "Copy Club ID". Grey outline/text by default; flips to
/// green when `success` is true. Hollow — the caller owns the transient timing (flip `success`
/// on tap, revert after a delay); this component only renders whichever state it's given.
/// Mirrors Android's `TriggerPill`.
public struct TriggerPill: View {
    let text: String
    let action: () -> Void
    var success: Bool
    var successText: String
    var enabled: Bool

    public init(
        text: String,
        action: @escaping () -> Void,
        success: Bool = false,
        successText: String? = nil,
        enabled: Bool = true
    ) {
        self.text = text
        self.action = action
        self.success = success
        self.successText = successText ?? text
        self.enabled = enabled
    }

    private var tint: Color { success ? KluvsTheme.colors.success : KluvsTheme.colors.contentMuted }

    public var body: some View {
        Button(action: action) {
            Text(success ? successText : text)
                .kluvsStyle(KluvsTheme.typography.finePrint)
                .foregroundColor(tint)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .overlay(Capsule().strokeBorder(tint, lineWidth: 1))
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    HStack(spacing: 12) {
        TriggerPill(text: "Copy Club ID", action: {}, successText: "Copied!")
        TriggerPill(text: "Copy Club ID", action: {}, enabled: false)
    }
    .padding()
}
