import SwiftUI
import DesignSystem

/// "Check your inbox" prompt shown after email sign up succeeds but requires email confirmation
/// before a session can be created. Unlike `AuthForgotSheet`, there is no form phase — by the
/// time this shows, sign up has already completed. Mirrors Android's `AuthSignUpConfirmationSheet`.
struct AuthSignupSheetModifier: ViewModifier {
    @Binding var isPresented: Bool
    let email: String
    let onDismiss: () -> Void

    func body(content: Content) -> some View {
        content
            .kluvsBottomSheet(
                isPresented: $isPresented,
                header: String(localized: "headline_check_your_inbox"),
                onDismiss: onDismiss,
                content: {
                    AuthSignupSheetSent(email: email)
                }
            )
    }
}

extension View {
    func authSignupSentSheet(isPresented: Binding<Bool>, email: String, onDismiss: @escaping () -> Void) -> some View {
        modifier(AuthSignupSheetModifier(isPresented: isPresented, email: email, onDismiss: onDismiss))
    }
}

private struct AuthSignupSheetSent: View {
    let email: String

    var body: some View {
        VStack(alignment: .center, spacing: 8) {
            Text(String(localized: "label_signup_confirmation_sent_to"))
                .kluvsStyle(KluvsTheme.typography.label)
                .foregroundColor(KluvsTheme.colors.contentMuted)

            Text(email)
                .kluvsStyle(KluvsTheme.typography.body.large)
                .fontWeight(.medium)
                .foregroundColor(KluvsTheme.colors.content)

            Text(String(localized: "text_signup_confirmation_body"))
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 8)
    }
}

#Preview {
    Color.warmDarkBase
        .ignoresSafeArea()
        .authSignupSentSheet(isPresented: .constant(true), email: "test@example.com", onDismiss: {})
}
