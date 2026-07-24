import SwiftUI

/// Low-emphasis action with no container (design-system "Ghost / Text" — see
/// design-system/docs/buttons.md), e.g. "Forgot password?" or "Cancel." Copper or grey text,
/// selected via `emphasized`. Mirrors Android's `TextButton`.
public struct TextButton: View {
    let text: String
    let action: () -> Void
    var enabled: Bool
    var emphasized: Bool

    public init(text: String, action: @escaping () -> Void, enabled: Bool = true, emphasized: Bool = false) {
        self.text = text
        self.action = action
        self.enabled = enabled
        self.emphasized = emphasized
    }

    public var body: some View {
        Button(action: action) {
            Text(text)
                .kluvsStyle(KluvsTheme.typography.label)
                .foregroundColor(emphasized ? KluvsTheme.colors.accent : KluvsTheme.colors.contentMuted)
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    VStack(spacing: 4) {
        TextButton(text: "Cancel", action: {})
        TextButton(text: "Forgot password?", action: {}, emphasized: true)
        TextButton(text: "Cancel", action: {}, enabled: false)
    }
    .padding()
}
