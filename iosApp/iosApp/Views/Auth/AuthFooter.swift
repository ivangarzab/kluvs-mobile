import SwiftUI
import SafariServices
import DesignSystem

/// Closing block for every auth screen — italic tagline + Privacy/Terms links, matching the
/// kluvs-frontend LoginPage footer. Mirrors Android's `AuthFooter`.
struct AuthFooter: View {
    @State private var safariUrl: URL?

    var body: some View {
        VStack(spacing: 0) {
            Rectangle().fill(KluvsTheme.colors.divider).frame(height: 1)

            Spacer().frame(height: 20)

            Text(String(localized: "text_reading_done_together"))
                .kluvsStyle(KluvsTheme.typography.title.small, feature: true)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .multilineTextAlignment(.center)

            Spacer().frame(height: 14)

            HStack(spacing: 10) {
                Text(String(localized: "privacy_policy").uppercased())
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                    .onTapGesture { safariUrl = URL(string: "https://kluvs.com/privacy") }

                Circle()
                    .fill(KluvsTheme.colors.divider)
                    .frame(width: 3, height: 3)

                Text(String(localized: "terms_of_use").uppercased())
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                    .onTapGesture { safariUrl = URL(string: "https://kluvs.com/terms") }
            }
        }
        .frame(maxWidth: .infinity)
        .sheet(item: $safariUrl) { url in
            AuthFooterSafariView(url: url)
                .ignoresSafeArea()
        }
    }
}

private struct AuthFooterSafariView: UIViewControllerRepresentable {
    let url: URL

    func makeUIViewController(context: Context) -> SFSafariViewController {
        let config = SFSafariViewController.Configuration()
        config.entersReaderIfAvailable = false
        let vc = SFSafariViewController(url: url, configuration: config)
        vc.preferredControlTintColor = UIColor(named: "AccentColor")
        return vc
    }

    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}
}

#Preview {
    AuthFooter()
        .padding(24)
        .background(Color.warmDarkBase)
}
