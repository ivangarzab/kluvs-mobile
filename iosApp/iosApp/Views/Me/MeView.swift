import SwiftUI
import Shared

struct MeView: View {
    let userId: String
    @StateObject private var viewModel = MeViewModelWrapper()
    @StateObject private var authViewModel = AuthViewModelWrapper()

    var body: some View {
        ZStack {
            if viewModel.isLoading {
                LoadingView()
                    .transition(.opacity)
            } else if let error = viewModel.error {
                ErrorView(message: error, onRetry: {
                    viewModel.loadUserData(userId: userId)
                })
                .transition(.opacity)
            } else {
                ScrollView {
                    VStack(spacing: 0) {
                        if let profile = viewModel.profile {
                            ProfileSection(profile: profile)
                        }

                        Divider()
                            .padding(.vertical, 8)

                        if let statistics = viewModel.statistics {
                            StatisticsSection(statistics: statistics)

                            Divider()
                                .padding(.vertical, 8)
                        }

                        CurrentlyReadingSection(currentReadings: viewModel.currentlyReading)

                        Divider()
                            .padding(.vertical, 8)

                        FooterSection(onSignOut: {
                            authViewModel.signOut()
                        })
                    }
                    .padding(16)
                }
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: viewModel.isLoading)
        .animation(.easeInOut(duration: 0.3), value: viewModel.error)
        .onAppear {
            viewModel.loadUserData(userId: userId)
        }
    }
}

// MARK: - Profile Section
struct ProfileSection: View {
    let profile: Shared.UserProfile

    var body: some View {
        HStack(alignment: .center, spacing: 16) {
            // Avatar placeholder
            Circle()
                .fill(Color.brandOrange)
                .frame(width: 60, height: 60)

            VStack(alignment: .leading, spacing: 4) {
                Text(profile.name)
                    .font(.body)
                    .fontWeight(.medium)

                Text(profile.handle ?? "")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                Text(String(format: NSLocalizedString("label_member_since", comment: ""), profile.joinDate))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()
        }
        .padding()
    }
}

// MARK: - Footer Section
struct FooterSection: View {
    let onSignOut: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            FooterItem(label: String(localized: "button_settings"), icon: .settings, action: {
                // TODO: Navigate to settings
            })

            Divider()
                .padding(.vertical, 8)

            FooterItem(label: String(localized: "button_help_support"), icon: .help, action: {
                // TODO: Navigate to help & support
            })

            Divider()
                .padding(.vertical, 8)

            FooterItem(label: String(localized: "sign_out"), icon: .logout, action: onSignOut)

            Divider()
                .padding(.vertical, 8)

            HStack {
                Spacer()
                Text(String(format: NSLocalizedString("app_version", comment: ""), "0.0.1")) //TODO: Get actual version from KMP
                    .font(.caption)
                    .italic()
                    .foregroundColor(.secondary)
                    .padding(.top, 8)
            }
            .padding(.horizontal, 16)
        }
    }
}

struct FooterItem: View {
    let label: String
    let icon: CustomIcon
    let action: (() -> Void)?

    init(label: String, icon: CustomIcon, action: (() -> Void)? = nil) {
        self.label = label
        self.icon = icon
        self.action = action
    }

    var body: some View {
        Button(action: {
            action?()
        }) {
            HStack(spacing: 12) {
                Image.custom(icon)
                    .font(.system(size: 20))
                    .foregroundColor(.brandOrange)
                    .frame(width: 24, height: 24)

                Text(label)
                    .font(.body)
                    .foregroundColor(.primary)

                Spacer()
            }
            .padding(.horizontal, 16)
        }
        .disabled(action == nil)
    }
}

#Preview {
    MeView(userId: "1")
}
