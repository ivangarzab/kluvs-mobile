import SwiftUI

/// Supporting/secondary action, "active" emphasis (design-system "Secondary / Outlined", copper
/// variant — see design-system/docs/buttons.md). Outlined copper border and text, 12pt radius.
/// Use `OutlinedButton` for the grey/muted variant of this same role. Mirrors Android's
/// `SecondaryButton`.
public struct SecondaryButton: View {
    let text: String
    let action: () -> Void
    var enabled: Bool
    var buttonColor: Color

    /// Stretches the button to the width offered by its container, the way Compose callers get
    /// with `Modifier.fillMaxWidth()`. Opt-in because the background/border is drawn around the
    /// label: an outer `.frame(maxWidth: .infinity)` widens only the tap target, leaving the
    /// visible pill content-sized. Default false, so existing inline buttons are unaffected.
    var fillWidth: Bool

    public init(
        text: String,
        action: @escaping () -> Void,
        enabled: Bool = true,
        buttonColor: Color = KluvsTheme.colors.accent,
        fillWidth: Bool = false
    ) {
        self.text = text
        self.action = action
        self.enabled = enabled
        self.buttonColor = buttonColor
        self.fillWidth = fillWidth
    }

    public var body: some View {
        Button(action: action) {
            Text(text)
                .kluvsStyle(KluvsTheme.typography.label)
                .foregroundColor(buttonColor)
                .padding(.vertical, 12)
                .padding(.horizontal, 24)
                .frame(maxWidth: fillWidth ? .infinity : nil)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(buttonColor, lineWidth: 1)
                )
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    VStack(spacing: 12) {
        SecondaryButton(text: "Change Role", action: {})
        SecondaryButton(text: "Change Role", action: {}, enabled: false)
        SecondaryButton(text: "Full Width", action: {}, fillWidth: true)
        SecondaryButton(text: "Delete club", action: {}, buttonColor: KluvsTheme.colors.danger)
    }
    .padding()
}
