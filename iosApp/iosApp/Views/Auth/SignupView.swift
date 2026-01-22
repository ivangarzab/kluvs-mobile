//
//  SignupView.swift
//  iosApp
//
//  Thin wrapper for signup mode
//
import SwiftUI

struct SignupView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    let onNavigateToLogin: () -> Void
    let onNavigateToForgotPassword: () -> Void

    var body: some View {
        Group {
            switch viewModel.authState {
            case .loading:
                LoadingView()
            case .oauthPending:
                LoadingView()
            case .authenticated:
                EmptyView()
            case .unauthenticated, .error:
                AuthFormView(
                    mode: .signup,
                    viewModel: viewModel,
                    onNavigate: { navigation in
                        switch navigation {
                        case .signIn:
                            onNavigateToLogin()
                        case .forgetPassword:
                            onNavigateToForgotPassword()
                        default:
                            break
                        }
                    }
                )
            }
        }
    }
}

#Preview {
    SignupView(
        onNavigateToLogin: {},
        onNavigateToForgotPassword: {}
    )
}
