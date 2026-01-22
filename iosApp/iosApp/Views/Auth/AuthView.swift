//
//  AuthView.swift
//  iosApp
//
//  Single view that toggles between login/signup modes (standard iOS pattern)
//
import SwiftUI

struct AuthView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()
    @StateObject private var oauthHandler = OAuthCallbackHandler.shared
    @State private var authMode: AuthMode = .login
    let onNavigateToForgotPassword: () -> Void

    var body: some View {
        Group {
            switch viewModel.authState {
            case .loading:
                LoadingView()
            case .authenticated:
                EmptyView() // Navigation handled by AppCoordinator
            case .oauthPending(let url):
                LoadingView()
                    .onAppear {
                        openOAuthUrl(url)
                    }
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
        .onChange(of: oauthHandler.callbackUrl) { _, newUrl in
            if let url = newUrl {
                viewModel.handleOAuthCallback(callbackUrl: url.absoluteString)
                oauthHandler.clearCallback()
            }
        }
    }

    private func openOAuthUrl(_ urlString: String) {
        guard let url = URL(string: urlString) else { return }
        UIApplication.shared.open(url) { success in
            if success {
                viewModel.onOAuthUrlLaunched()
            }
        }
    }
}

#Preview {
    AuthView(onNavigateToForgotPassword: {})
}
