import SwiftUI
import Shared

struct ContentView: View {
    @StateObject private var appCoordinator = AppCoordinatorWrapper()
    @State private var navigationPath = NavigationPath()

    var body: some View {
        NavigationStack(path: $navigationPath) {
            Group {
                switch appCoordinator.navigationState {
                case .initializing:
                    LoadingView()
                case .unauthenticated:
                    AuthView(
                        onNavigateToForgotPassword: {
                            navigationPath.append(AuthRoute.forgotPassword)
                        }
                    )
                case .authenticated(let userId):
                    MainView(userId: userId)
                }
            }
            .navigationDestination(for: AuthRoute.self) { route in
                switch route {
                case .forgotPassword:
                    ForgotPasswordView()
                }
            }
        }
        .onChange(of: appCoordinator.navigationState) { _, newState in
            // Clear navigation stack when auth state changes
            if case .authenticated = newState {
                navigationPath = NavigationPath()
            } else if case .unauthenticated = newState {
                navigationPath = NavigationPath()
            }
        }
    }
}

enum AuthRoute: Hashable {
    case forgotPassword
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
