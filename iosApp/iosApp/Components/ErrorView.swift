import SwiftUI
import DesignSystem

struct ErrorView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(message)
                .kluvsStyle(KluvsTheme.typography.body.large)
                .foregroundColor(KluvsTheme.colors.danger)
                .multilineTextAlignment(.center)
                .padding(.horizontal)

            PrimaryButton(text: String(localized: "button_retry"), action: onRetry)

            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    ErrorView(message: "This is an error message", onRetry: {})
}
