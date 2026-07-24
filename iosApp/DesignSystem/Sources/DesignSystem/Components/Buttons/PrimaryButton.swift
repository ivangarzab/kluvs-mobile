import SwiftUI

/// The single most important action on a screen (design-system "Primary", see
/// design-system/docs/buttons.md). Copper fill, white text, 12pt radius. Mirrors Android's
/// `PrimaryButton`.
///
/// Deliberately does not override the icon/text color per-caller: `KluvsTheme.colors.onAccent`
/// (white) already has correct, DS-canonical contrast against the copper fill in both themes.
public struct PrimaryButton: View {
    let text: String
    let action: () -> Void
    var enabled: Bool
    var icon: IconType?

    public init(text: String, action: @escaping () -> Void, enabled: Bool = true, icon: IconType? = nil) {
        self.text = text
        self.action = action
        self.enabled = enabled
        self.icon = icon
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if let icon {
                    Icon(type: icon, contentDescription: nil, tint: KluvsTheme.colors.onAccent)
                        .frame(width: 18, height: 18)
                }
                Text(text).kluvsStyle(KluvsTheme.typography.label)
            }
            .foregroundColor(KluvsTheme.colors.onAccent)
            .padding(.vertical, 12)
            .padding(.horizontal, 24)
            .background(KluvsTheme.colors.accent)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    VStack(spacing: 12) {
        PrimaryButton(text: "Continue", action: {})
        PrimaryButton(text: "Create Session", action: {}, icon: .add)
        PrimaryButton(text: "Continue", action: {}, enabled: false)
    }
    .padding()
}
