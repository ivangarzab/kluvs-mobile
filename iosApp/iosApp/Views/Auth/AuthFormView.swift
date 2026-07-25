import AuthenticationServices
import SwiftUI
import Shared
//
//  AuthFormView.swift
//  iosApp
//
//  Main auth form UI (equivalent to Android's AuthFormContent)
//
import DesignSystem

struct AuthFormView: View {
    let mode: AuthMode
    @ObservedObject var viewModel: AuthViewModelWrapper
    let onNavigate: (LoginNavigation) -> Void

    @FocusState private var focusedField: Field?
    @State private var showErrorAlert = false
    @State private var errorMessage: String?

    enum Field {
        case email, password, confirmPassword
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Spacer()
                    .frame(height: 24)

                // Header
                Text("Welcome to your Kluvs")
                    .font(.title2)
                    .fontWeight(.bold)

                Text(mode == .login ? "Sign in to your account" : "Create a new account")
                    .font(.body)
                    .foregroundColor(.secondary)

                SocialButton(
                    text: String(localized: "button_continue_apple"),
                    icon: IconType.apple,
                    backgroundColor: .black,
                    textColor: .white,
                    action: {
                        AppleSignInHandler.shared.signIn { result in
                            switch result {
                            case .success(let idToken):
                                viewModel.signInWithApple(idToken: idToken)
                            case .failure(let error):
                                // Don't show error for user cancellation
                                if let authError = error as? ASAuthorizationError,
                                   authError.code == .canceled {
                                    return
                                }
                                errorMessage = error.localizedDescription
                                showErrorAlert = true
                            }
                        }
                    }
                )

                SocialButton(
                    text: String(localized: "button_continue_discord"),
                    icon: IconType.discord,
                    backgroundColor: .discordBlue,
                    textColor: .white,
                    action: {
                        viewModel.signInWithDiscord()
                    }
                )

                SocialButton(
                    text: String(localized: "button_continue_google"),
                    icon: IconType.google,
                    backgroundColor: .googleGray,
                    textColor: .googleTextGray,
                    action: {
                        viewModel.signInWithGoogle()
                    },
                    iconSize: 24
                )

                TextDividerView(text: String(localized: "hint_or_continue_email"))

                // Email field
                InputField(
                    label: String(localized: "label_email"),
                    value: Binding(
                        get: { viewModel.emailField },
                        set: { viewModel.onEmailChanged($0) }
                    ),
                    error: viewModel.emailError,
                    helperText: viewModel.emailError == nil ? String(localized: "hint_email") : nil,
                    keyboardType: .emailAddress,
                    submitLabel: .next,
                    onSubmit: { focusedField = .password }
                )
                .focused($focusedField, equals: .email)

                // Password field
                PasswordField(
                    label: String(localized: "label_password"),
                    value: Binding(
                        get: { viewModel.passwordField },
                        set: { viewModel.onPasswordChanged($0) }
                    ),
                    error: viewModel.passwordError,
                    helperText: viewModel.passwordError == nil ? (mode == .login ? String(localized: "hint_password_login") : String(localized: "hint_password_signup")) : nil,
                    submitLabel: mode == .login ? .go : .next,
                    onSubmit: {
                        if mode == .login {
                            viewModel.signIn()
                        } else {
                            focusedField = .confirmPassword
                        }
                    }
                )
                .focused($focusedField, equals: .password)

                // Confirm password (signup only)
                if mode == .signup {
                    PasswordField(
                        label: String(localized: "label_confirm_password"),
                        value: Binding(
                            get: { viewModel.confirmPasswordField },
                            set: { viewModel.onConfirmPasswordChanged($0) }
                        ),
                        error: viewModel.confirmPasswordError,
                        helperText: viewModel.confirmPasswordError == nil ? String(localized: "hint_confirm_password") : nil,
                        submitLabel: .go,
                        onSubmit: { viewModel.signUp() }
                    )
                    .focused($focusedField, equals: .confirmPassword)
                }

                // Forgot password (login only)
                if mode == .login {
                    HStack {
                        Spacer()
                        Button("Forgot password?") {
                            onNavigate(.forgetPassword)
                        }
                        .font(.body)
                        .foregroundColor(.brandOrange)
                    }
                    .frame(height: 20) // Fixed height to match signup spacing
                }

                // Submit button
                Button(action: {
                    if mode == .login {
                        viewModel.signIn()
                    } else {
                        viewModel.signUp()
                    }
                }) {
                    Text(mode == .login ? "Sign In" : "Sign Up")
                        .font(.body)
                        .fontWeight(.medium)
                        .foregroundColor(Color(uiColor: .systemBackground))
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.brandOrange)
                        .cornerRadius(8)
                }

                // Navigation link
                HStack(spacing: 4) {
                    Text(mode == .login ? "Don't have an account?" : "Already have an account?")
                        .foregroundColor(.secondary)

                    Button(mode == .login ? "Sign Up" : "Sign In") {
                        onNavigate(mode == .login ? .signUp : .signIn)
                    }
                    .font(.body)
                    .fontWeight(.medium)
                    .foregroundColor(.brandOrange)
                }

                Spacer()
            }
            .padding(16)
        }
        .onChange(of: viewModel.authState) { _, newState in
            if case .error(let error) = newState {
                errorMessage = error.toLocalizedMessage()
                showErrorAlert = true
            }
        }
        .kluvsConfirmationDialog(
            isPresented: $showErrorAlert,
            title: "Authentication Error",
            message: errorMessage ?? "An unexpected error occurred",
            confirmLabel: "OK",
            dismissLabel: nil,
            onConfirm: {}
        )
    }
}

enum AuthMode {
    case login
    case signup
}

enum LoginNavigation {
    case signIn
    case signUp
    case forgetPassword
}

#Preview {
    AuthFormView(
        mode: .login,
        viewModel: AuthViewModelWrapper(),
        onNavigate: { _ in }
    )
}
