//
//  AuthView.swift
//  iosApp
//
//  Single view that toggles between login/signup modes (standard iOS pattern)
//
import SwiftUI

struct AuthView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @State private var authMode: AuthMode = .login
    let onNavigateToForgotPassword: () -> Void

    var body: some View {
        Group {
            switch viewModel.authState {
            case .loading:
                LoadingView()
            case .authenticated:
                EmptyView() // Navigation handled by AppCoordinator
            case .unauthenticated, .error:
                AuthFormView(
                    mode: authMode,
                    viewModel: viewModel,
                    onNavigate: { navigation in
                        switch navigation {
                        case .signUp:
                            withAnimation {
                                authMode = .signup
                            }
                        case .signIn:
                            withAnimation {
                                authMode = .login
                            }
                        case .forgetPassword:
                            onNavigateToForgotPassword()
                        }
                    }
                )
            }
        }
    }
}

#Preview {
    AuthView(onNavigateToForgotPassword: {})
}
