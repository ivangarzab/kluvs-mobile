import SwiftUI
import DesignSystem

/// Auth-screen masthead — brand mark, voice-phrase headline (italic, "the featured thing" per
/// design-system/docs/typography.md's `feature` modifier), and an optional subhead. Shared
/// across sign in, sign up, and the forgot-password sheet. Mirrors Android's `AuthMasthead`.
struct AuthMasthead: View {
    let voicePhrase: String
    var subhead: String? = nil

    var body: some View {
        VStack(spacing: 0) {
            Image("kluvs_mark")
                .resizable()
                .scaledToFit()
                .frame(height: 48)

            Spacer().frame(height: 20)

            Text(voicePhrase)
                .kluvsStyle(KluvsTheme.typography.headline.small, feature: true)
                .foregroundColor(KluvsTheme.colors.content)
                .multilineTextAlignment(.center)

            if let subhead {
                Spacer().frame(height: 10)

                Text(subhead)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: 280)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

#Preview {
    AuthMasthead(
        voicePhrase: "Welcome back",
        subhead: "Sign in to keep reading together."
    )
    .padding(24)
    .background(Color.warmDarkBase)
}
