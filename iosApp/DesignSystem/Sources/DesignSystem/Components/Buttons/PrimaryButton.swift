import SwiftUI

/// The single most important action on a screen (design-system "Primary", see
/// design-system/docs/buttons.md). Copper fill, white text, 12pt radius. Mirrors Android's
/// `PrimaryButton`.
///
/// Deliberately does not override the icon/text color per-caller: `KluvsTheme.colors.onAccent`
/// (white) already has correct, DS-canonical contrast against the copper fill in both themes.
///
/// Disabled state mirrors M3's own `ButtonDefaults` disabled colors, which Android's
/// `PrimaryButton` inherits for free by not overriding `colors:` — `disabledContainerColor` =
/// `onSurface` at 12% alpha, `disabledContentColor` = `onSurface` at 38% alpha (`onSurface` is
/// `KluvsTheme.colors.content` here, per Theme.kt's role mapping). A flat `.opacity()` dim of the
/// full copper fill looked wrong because it kept the copper hue instead of Android's neutral gray.
public struct PrimaryButton: View {
    let text: String
    let action: () -> Void
    var enabled: Bool
    var icon: IconType?
    /// Stretches the button to the width offered by its container, the way Compose callers get
    /// with `Modifier.fillMaxWidth()`. Opt-in because the background/border is drawn around the
    /// label: an outer `.frame(maxWidth: .infinity)` widens only the tap target, leaving the
    /// visible pill content-sized. Default false, so existing inline buttons are unaffected.
    var fillWidth: Bool

    public init(
        text: String,
        action: @escaping () -> Void,
        enabled: Bool = true,
        icon: IconType? = nil,
        fillWidth: Bool = false
    ) {
        self.text = text
        self.action = action
        self.enabled = enabled
        self.icon = icon
        self.fillWidth = fillWidth
    }

    private var containerColor: Color {
        enabled ? KluvsTheme.colors.accent : KluvsTheme.colors.content.opacity(0.12)
    }

    private var contentColor: Color {
        enabled ? KluvsTheme.colors.onAccent : KluvsTheme.colors.content.opacity(0.38)
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if let icon {
                    Icon(type: icon, contentDescription: nil, tint: contentColor)
                        .frame(width: 18, height: 18)
                }
                Text(text).kluvsStyle(KluvsTheme.typography.label)
            }
            .foregroundColor(contentColor)
            .padding(.vertical, 12)
            .padding(.horizontal, 24)
            .frame(maxWidth: fillWidth ? .infinity : nil)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .disabled(!enabled)
    }
}

#Preview {
    VStack(spacing: 12) {
        PrimaryButton(text: "Continue", action: {})
        PrimaryButton(text: "Create Session", action: {}, icon: .add)
        PrimaryButton(text: "Continue", action: {}, enabled: false)
        PrimaryButton(text: "Full Width", action: {}, fillWidth: true)
    }
    .padding()
}
