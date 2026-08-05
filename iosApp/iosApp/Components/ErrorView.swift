import SwiftUI
import DesignSystem

/// Full-screen "couldn't load this" state — reuses `EmptyState`'s Fragmented Hex Grid shell with
/// `danger` line color so a load failure reads as a distinct signal from an empty-but-healthy
/// screen, rather than a floating text+button popup. Mirrors Android's `ErrorScreen` composable.
struct ErrorView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        EmptyState(
            heading: String(localized: "error_something_went_wrong"),
            body: message,
            lineColor: KluvsTheme.colors.danger
        ) {
            PrimaryButton(text: String(localized: "button_retry"), action: onRetry)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    ErrorView(message: "This is an error message", onRetry: {})
}
