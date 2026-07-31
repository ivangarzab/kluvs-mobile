import SwiftUI
import DesignSystem

/// Inline auth-form error, matching kluvs-frontend's persistent ErrorBanner — replaces the
/// transient alert dialog previously used here, since form errors (invalid credentials, rate
/// limits) need to stay visible until the user acts, not require a tap to dismiss. Mirrors
/// Android's `ErrorBanner`.
struct ErrorBanner: View {
    let message: String

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 14))
                .foregroundColor(KluvsTheme.colors.danger)

            Text(message)
                .kluvsStyle(KluvsTheme.typography.caption)
                .foregroundColor(KluvsTheme.colors.danger)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .background(KluvsTheme.colors.dangerSubtle)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(KluvsTheme.colors.danger, lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

#Preview {
    ErrorBanner(message: "Invalid email or password")
        .padding(16)
        .background(Color.warmDarkBase)
}
