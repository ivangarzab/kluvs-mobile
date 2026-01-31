import SwiftUI
import Shared
import PhotosUI

struct MeView: View {
    let userId: String
    @StateObject private var viewModel = MeViewModelWrapper()

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
                            ProfileSection(
                                profile: profile,
                                isUploadingAvatar: viewModel.isUploadingAvatar,
                                onAvatarPicked: { imageData in
                                    viewModel.uploadAvatar(imageData: imageData)
                                }
                            )
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
                            viewModel.onSignOutClicked()
                        })
                    }
                    .padding(16)
                }
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: viewModel.isLoading)
        .animation(.easeInOut(duration: 0.3), value: viewModel.error)
        .overlay(alignment: .bottom) {
            if let snackbarError = viewModel.snackbarError {
                SnackbarView(message: snackbarError) {
                    viewModel.clearAvatarError()
                }
                .padding()
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.easeInOut(duration: 0.3), value: viewModel.snackbarError)
        .onAppear {
            viewModel.loadUserData(userId: userId)
        }
        .alert(isPresented: $viewModel.showLogoutConfirmation) {
            Alert(
                title: Text(NSLocalizedString("logout_confirmation_title", comment: "")),
                message: Text(NSLocalizedString("logout_confirmation_message", comment: "")),
                primaryButton: .destructive(Text(NSLocalizedString("yes", comment: ""))) {
                    viewModel.onSignOutDialogConfirmed()
                },
                secondaryButton: .cancel(Text(NSLocalizedString("no", comment: ""))) {
                    viewModel.onSignOutDialogDismissed()
                }
            )
        }
    }
}

// MARK: - Profile Section
struct ProfileSection: View {
    let profile: Shared.UserProfile
    var isUploadingAvatar: Bool = false
    var onAvatarPicked: ((Data) -> Void)? = nil

    @State private var selectedItem: PhotosPickerItem? = nil

    var body: some View {
        HStack(alignment: .center, spacing: 16) {
            // Avatar with edit button
            ZStack(alignment: .bottomTrailing) {
                MemberAvatar(
                    avatarUrl: profile.avatarUrl,
                    size: 60,
                    isLoading: isUploadingAvatar,
                    onClick: nil
                )

                // Edit button overlay
                PhotosPicker(selection: $selectedItem, matching: .images) {
                    ZStack {
                        Circle()
                            .fill(Color.brandOrange.opacity(0.9))
                            .frame(width: 24, height: 24)

                        Image.custom(CustomIcon.edit)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 12, height: 12)
                            .foregroundColor(.white)
                    }
                }
                .onChange(of: selectedItem) { newItem in
                    Task {
                        if let data = try? await newItem?.loadTransferable(type: Data.self) {
                            let compressedData = compressImage(data)
                            onAvatarPicked?(compressedData)
                        }
                    }
                }
            }

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

// MARK: - Snackbar View
struct SnackbarView: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        HStack {
            Text(message)
                .font(.body)
                .foregroundColor(.white)
                .lineLimit(2)

            Spacer()

            Button(action: onDismiss) {
                Image(systemName: "xmark")
                    .foregroundColor(.white)
            }
        }
        .padding()
        .background(Color.red.opacity(0.9))
        .cornerRadius(8)
        .shadow(radius: 4)
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                onDismiss()
            }
        }
    }
}

#Preview {
    MeView(userId: "1")
}
