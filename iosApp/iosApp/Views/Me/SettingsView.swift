import SwiftUI
import SafariServices
import DesignSystem

struct SettingsView: View {
    let userId: String
    @StateObject private var viewModel = SettingsViewModelWrapper()
    @State private var showSaveSuccess = false
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                EditProfileSection(
                    editedName: Binding(
                        get: { viewModel.editedName },
                        set: { viewModel.onNameChanged($0) }
                    ),
                    editedHandle: Binding(
                        get: { viewModel.editedHandle },
                        set: { viewModel.onHandleChanged($0) }
                    ),
                    hasChanges: viewModel.hasChanges,
                    isSaving: viewModel.isSaving,
                    saveError: viewModel.saveError,
                    onSaveProfile: { viewModel.onSaveProfile() }
                )

                Divider()
                    .overlay(KluvsTheme.colors.divider)
                    .padding(.top, 12)

                LegalSection()

                AboutSection()
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
        .background(KluvsTheme.colors.background)
        .navigationTitle(String(localized: "settings_title"))
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: { dismiss() }) {
                    IconType.back.image
                }
            }
        }
        .onAppear {
            viewModel.loadProfile(userId: userId)
        }
        .onChange(of: viewModel.saveSuccess) { success in
            if success {
                showSaveSuccess = true
                viewModel.onDismissSaveSuccess()
            }
        }
        .overlay(alignment: .bottom) {
            if showSaveSuccess {
                SaveSuccessToast {
                    showSaveSuccess = false
                }
                .padding()
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.easeInOut(duration: 0.3), value: showSaveSuccess)
    }
}

// MARK: - Edit Profile Section

struct EditProfileSection: View {
    @Binding var editedName: String
    @Binding var editedHandle: String
    let hasChanges: Bool
    let isSaving: Bool
    let saveError: String?
    let onSaveProfile: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(String(localized: "edit_profile").uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(KluvsTheme.colors.contentMuted)

            Spacer()
                .frame(height: 4)

            InputField(label: String(localized: "label_name"), value: $editedName)

            InputField(label: String(localized: "label_handle"), value: $editedHandle, prefix: "@")

            if let saveError = saveError {
                Text(saveError)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.danger)
                    .padding(.top, 4)
            }

            PrimaryButton(
                text: isSaving ? String(localized: "button_save") + "…" : String(localized: "button_save"),
                action: onSaveProfile,
                enabled: hasChanges && !isSaving
            )
            .frame(maxWidth: .infinity)
            .padding(.top, 4)
        }
        .padding(.vertical, 12)
    }
}

// MARK: - Legal Section

struct LegalSection: View {
    @State private var safariUrl: URL? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(String(localized: "legal_title").uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .padding(.bottom, 8)

            LegalRow(label: String(localized: "privacy_policy")) {
                safariUrl = URL(string: "https://kluvs.com/privacy")
            }
            Divider().overlay(KluvsTheme.colors.divider)

            LegalRow(label: String(localized: "terms_of_use")) {
                safariUrl = URL(string: "https://kluvs.com/terms")
            }
            Divider().overlay(KluvsTheme.colors.divider)
        }
        .padding(.vertical, 12)
        .sheet(item: $safariUrl) { url in
            SafariView(url: url)
                .ignoresSafeArea()
        }
    }
}

private struct LegalRow: View {
    let label: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                Text(label)
                    .kluvsStyle(KluvsTheme.typography.label)
                    .foregroundColor(KluvsTheme.colors.accent)
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
            .padding(.vertical, 12)
        }
    }
}

// MARK: - Safari View

private struct SafariView: UIViewControllerRepresentable {
    let url: URL

    func makeUIViewController(context: Context) -> SFSafariViewController {
        let config = SFSafariViewController.Configuration()
        config.entersReaderIfAvailable = false
        let vc = SFSafariViewController(url: url, configuration: config)
        vc.preferredControlTintColor = UIColor(named: "AccentColor")
        return vc
    }

    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}
}

extension URL: @retroactive Identifiable {
    public var id: String { absoluteString }
}

// MARK: - About Section

struct AboutSection: View {
    private var appVersion: String {
        Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
    }

    var body: some View {
        HStack {
            Spacer()
            Text(String(format: NSLocalizedString("app_version", comment: ""), appVersion))
                .kluvsStyle(KluvsTheme.typography.finePrint)
                .italic()
                .foregroundColor(KluvsTheme.colors.contentMuted)
        }
        .padding(.vertical, 12)
    }
}

// MARK: - Save Success Toast

private struct SaveSuccessToast: View {
    let onDismiss: () -> Void

    var body: some View {
        HStack {
            Text(String(localized: "save_success"))
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
        .background(Color.green.opacity(0.9))
        .cornerRadius(8)
        .shadow(radius: 4)
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                onDismiss()
            }
        }
    }
}

#Preview {
    NavigationStack {
        SettingsView(userId: "1")
    }
}
