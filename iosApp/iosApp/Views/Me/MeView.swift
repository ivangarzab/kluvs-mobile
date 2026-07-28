import SwiftUI
import Shared
import PhotosUI
import DesignSystem

struct MeView: View {
    let userId: String
    @StateObject private var viewModel = MeViewModelWrapper()
    @State private var showSettings = false
    @State private var editingShelfItem: Shared.ShelfItem? = nil
    @State private var progressType: Shared.ProgressType = .page
    @State private var progressCurrentPageText = ""
    @State private var progressPercentText = ""
    @State private var progressMarkFinished = false
    @State private var progressLastAutoTriggerValue: String? = nil
    @State private var progressSheetHeader = "Track Progress"

    var body: some View {
        VStack(spacing: 0) {
            MeTopBar(
                onReadingLogClick: { viewModel.onReadingLogClicked() },
                onSettingsClick: { showSettings = true },
                onSignOutClick: { viewModel.onSignOutClicked() }
            )

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
                                StatisticsSection(statistics: statistics, joinDate: viewModel.profile?.joinDate)

                                Divider()
                                    .padding(.vertical, 8)
                            }

                            if viewModel.upNext != nil {
                                UpNextSection(upNext: viewModel.upNext)

                                Divider()
                                    .padding(.vertical, 8)
                            }

                            ShelfSection(
                                shelf: viewModel.shelf,
                                onUpdateProgress: { sessionId in
                                    let item = viewModel.shelf.first { $0.sessionId == sessionId }
                                    progressType = item?.ownProgress?.type ?? .page
                                    let currentPage: Int32? = item?.ownProgress?.currentPage?.int32Value
                                    progressCurrentPageText = currentPage.map { String($0) } ?? ""
                                    let percentComplete: Float? = item?.ownProgress?.percentComplete?.floatValue
                                    progressPercentText = percentComplete.map { formatPercent($0) } ?? ""
                                    progressMarkFinished = item?.ownProgress?.isCompleted ?? false
                                    progressLastAutoTriggerValue = nil
                                    progressSheetHeader = item?.ownProgress != nil ? "Update Progress" : "Track Progress"
                                    editingShelfItem = item
                                }
                            )

                            FooterSection()
                                .padding(.bottom, 24)
                        }
                        .padding(.horizontal, 16)
                    }
                    .transition(.opacity)
                }
            }
            .animation(.easeInOut(duration: 0.3), value: viewModel.isLoading)
            .animation(.easeInOut(duration: 0.3), value: viewModel.error)
        }
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
        .kluvsConfirmationDialog(
            isPresented: $viewModel.showLogoutConfirmation,
            title: NSLocalizedString("logout_confirmation_title", comment: ""),
            message: NSLocalizedString("logout_confirmation_message", comment: ""),
            confirmLabel: NSLocalizedString("yes", comment: ""),
            dismissLabel: NSLocalizedString("no", comment: ""),
            isDestructive: true,
            onDismiss: { viewModel.onSignOutDialogDismissed() },
            onConfirm: { viewModel.onSignOutDialogConfirmed() }
        )
        .sheet(isPresented: $showSettings) {
            NavigationStack {
                SettingsView(userId: userId)
            }
        }
        .kluvsBottomSheet(item: $editingShelfItem, header: progressSheetHeader) { item in
            ReadingProgressFields(
                bookTitle: item.bookTitle,
                pageCount: item.bookPageCount?.int32Value,
                progressType: $progressType,
                currentPageText: $progressCurrentPageText,
                percentText: $progressPercentText,
                markFinished: $progressMarkFinished,
                lastAutoTriggerValue: $progressLastAutoTriggerValue
            )
        } footer: { item in
            let canSave: Bool = {
                switch progressType {
                case .page: return Int32(progressCurrentPageText) != nil
                case .percent: return Float(progressPercentText) != nil
                default: return false
                }
            }()
            BottomSheetFooter(
                actionLabel: "Save Progress",
                onAction: {
                    let page = progressType == .page ? Int32(progressCurrentPageText) : nil
                    let percent: Int32? = progressType == .percent
                        ? Float(progressPercentText).map { Int32(Swift.min(100, Swift.max(0, $0.rounded()))) }
                        : nil
                    viewModel.onSaveProgress(
                        sessionId: item.sessionId,
                        type: progressType,
                        currentPage: page,
                        percentComplete: percent,
                        markFinished: progressMarkFinished
                    )
                    editingShelfItem = nil
                },
                onCancel: { editingShelfItem = nil },
                actionEnabled: canSave
            )
        }
        .kluvsBottomSheet(
            isPresented: $viewModel.showReadingLog,
            header: "Reading Log",
            onDismiss: { viewModel.onReadingLogDismissed() }
        ) {
            ReadingLogFields(log: viewModel.readingLog, isLoading: viewModel.isReadingLogLoading)
        }
    }
}

extension Shared.ShelfItem: @retroactive Identifiable {
    public var id: String { sessionId }
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

                        IconType.edit.image
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
            }

            Spacer()
        }
        .padding()
    }
}

// MARK: - Footer Section
struct FooterSection: View {
    var body: some View {
        VStack(spacing: 0) {
            Divider()
                .padding(.vertical, 12)

            FooterItem(label: String(localized: "button_help_support"), icon: .help, action: {
                // TODO: Navigate to help & support
            })
        }
    }
}

struct FooterItem: View {
    let label: String
    let icon: IconType
    let action: (() -> Void)?
    var labelColor: Color = .primary
    var iconColor: Color = .primary

    init(label: String, icon: IconType, labelColor: Color = .primary, iconColor: Color = .primary, action: (() -> Void)? = nil) {
        self.label = label
        self.icon = icon
        self.labelColor = labelColor
        self.iconColor = iconColor
        self.action = action
    }

    var iconSize = 18.0

    var body: some View {
        Button(action: {
            action?()
        }) {
            HStack(spacing: 12) {
                icon.image
                    .resizable()
                    .scaledToFit()
                    .frame(width: iconSize, height: iconSize)
                    .foregroundColor(iconColor)

                Text(label)
                    .font(.body)
                    .foregroundColor(labelColor)

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
