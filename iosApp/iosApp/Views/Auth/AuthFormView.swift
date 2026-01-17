//
//  AuthFormView.swift
//  iosApp
//
//  Main auth form UI (equivalent to Android's AuthFormContent)
//
import SwiftUI
import Shared

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

                Spacer()
                    .frame(height: 12)
                
                SocialButtonView(
                    text: "Continue with Discord",
                    iconName: CustomIcon.discord,
                    iconSize: 20,
                    backgroundColor: .discordBlue,
                    textColor: .white,
                    action: {
                        // TODO: Implement OAuth
                    }
                )

                SocialButtonView(
                    text: "Continue with Google",
                    iconName: CustomIcon.google,
                    iconSize: 24,
                    backgroundColor: .googleGray,
                    textColor: .googleTextGray,
                    action: {
                        // TODO: Implement OAuth
                    }
                )

                TextDividerView(text: "or continue with email")

                // Email field
                InputFieldView(
                    label: "Email",
                    text: Binding(
                        get: { viewModel.emailField },
                        set: { viewModel.onEmailChanged($0) }
                    ),
                    icon: .email,
                    supportingText: viewModel.emailError ?? "Enter a valid email address",
                    supportingTextColor: viewModel.emailError != nil ? .red : .gray,
                    keyboardType: .emailAddress,
                    submitLabel: .next,
                    onSubmit: { focusedField = .password }
                )
                .focused($focusedField, equals: .email)

                // Password field
                InputFieldView(
                    label: "Password",
                    text: Binding(
                        get: { viewModel.passwordField },
                        set: { viewModel.onPasswordChanged($0) }
                    ),
                    icon: .password,
                    supportingText: viewModel.passwordError ?? (mode == .login ? "Minimum 6 characters" : "Minimum 8 characters"),
                    supportingTextColor: viewModel.passwordError != nil ? .red : .gray,
                    isPassword: true,
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
                    InputFieldView(
                        label: "Confirm Password",
                        text: Binding(
                            get: { viewModel.confirmPasswordField },
                            set: { viewModel.onConfirmPasswordChanged($0) }
                        ),
                        icon: .password,
                        supportingText: viewModel.confirmPasswordError ?? "Must match password above",
                        supportingTextColor: viewModel.confirmPasswordError != nil ? .red : .gray,
                        isPassword: true,
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
                } else {
                    Spacer()
                        .frame(height: 16)
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
                        .foregroundColor(.black)
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
        .alert("Authentication Error", isPresented: $showErrorAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(errorMessage ?? "An unexpected error occurred")
        }
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
