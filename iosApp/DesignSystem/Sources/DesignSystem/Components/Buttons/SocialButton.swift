import SwiftUI

/// Fixed brand-branded button for OAuth sign-in (design-system "Social / OAuth" — see
/// design-system/docs/buttons.md). Colors are per-provider and passed in by the caller, not
/// themed — Discord/Google/Apple each have a fixed brand fill regardless of light/dark theme.
/// Mirrors Android's `SocialButton` (height/radius/typography match Android's current values, not
/// this file's own pre-existing iOS values — 48pt height/8pt radius/plain `.body` font were stale,
/// same drift Android caught and fixed on its own side before this port).
///
/// Ported from the app's own `SocialButtonView` (dropped the `-View` suffix — matches every other
/// design-system component's naming, Android included, and keeps the vocabulary the same across
/// platforms).
public struct SocialButton: View {
    let text: String
    let icon: IconType
    let backgroundColor: Color
    let textColor: Color
    let action: () -> Void
    var enabled: Bool
    var height: CGFloat
    var iconSize: CGFloat

    public init(
        text: String,
        icon: IconType,
        backgroundColor: Color,
        textColor: Color,
        action: @escaping () -> Void,
        enabled: Bool = true,
        height: CGFloat = 40,
        iconSize: CGFloat = 20
    ) {
        self.text = text
        self.icon = icon
        self.backgroundColor = backgroundColor
        self.textColor = textColor
        self.action = action
        self.enabled = enabled
        self.height = height
        self.iconSize = iconSize
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                // Apple's mark is a template asset (tint to white); the others carry their own
                // brand colors and must render unmodified — same reasoning as Android's
                // `SocialButton`, which reads `painterResource(...)` directly instead of going
                // through the shared, always-tinted `Icon` composable.
                if icon == .apple {
                    icon.image
                        .resizable()
                        .renderingMode(.template)
                        .scaledToFit()
                        .frame(width: iconSize, height: iconSize)
                        .foregroundColor(.white)
                } else {
                    icon.image
                        .resizable()
                        .renderingMode(.original)
                        .scaledToFit()
                        .frame(width: iconSize, height: iconSize)
                }

                Text(text)
                    .kluvsStyle(KluvsTheme.typography.label)
                    .foregroundColor(textColor)
            }
            .frame(maxWidth: .infinity)
            .frame(height: height)
            .background(backgroundColor)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    VStack(spacing: 12) {
        SocialButton(
            text: "Continue with Discord",
            icon: .discord,
            backgroundColor: .discordBlue,
            textColor: .white,
            action: {}
        )
        SocialButton(
            text: "Continue with Google",
            icon: .google,
            backgroundColor: .googleGray,
            textColor: .googleTextGray,
            action: {}
        )
        SocialButton(
            text: "Continue with Google",
            icon: .google,
            backgroundColor: .googleGray,
            textColor: .googleTextGray,
            action: {},
            enabled: false
        )
    }
    .padding()
}
