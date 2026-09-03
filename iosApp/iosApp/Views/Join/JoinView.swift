import SwiftUI
import Shared
import DesignSystem

/// Join-by-invite-token screen, reached by opening an invite Universal Link. "Join with a code"
/// inside the (already-authenticated) Clubs tab opens `JoinFields` in a local bottom sheet
/// instead — a deep link needs to land somewhere before auth resolves and needs the
/// `onNeedsSignIn` handoff below, neither of which a nested bottom sheet can do. Mirrors
/// Android's `JoinScreen`.
///
/// When `initialToken` is present the code is pre-filled and previewed immediately, so a
/// recipient who tapped a link sees the club rather than an empty text field.
///
/// The preview shows only the club name — `Shared.ClubPreview` has no avatar/member-count yet
/// (also a follow-up, needs a backend spec change).
struct JoinView: View {
    var initialToken: String? = nil
    let onNavigateToClub: (String) -> Void
    let onNeedsSignIn: (String) -> Void

    @StateObject private var viewModel = JoinViewModelWrapper()
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            TopAppBar(header: "Join a club", onNavigateBack: { dismiss() })

            VStack(alignment: .leading, spacing: 16) {
                InputField(
                    label: "Invite code",
                    value: Binding(
                        get: { viewModel.tokenInput },
                        set: { viewModel.onTokenChanged($0) }
                    )
                )
                .frame(maxWidth: .infinity)

                SecondaryButton(
                    text: "Preview",
                    action: { viewModel.previewInvite() },
                    enabled: !viewModel.tokenInput.trimmingCharacters(in: .whitespaces).isEmpty && !viewModel.isLoadingPreview,
                    fillWidth: true
                )

                if viewModel.isLoadingPreview {
                    LoadingSpinner()
                }

                if let previewError = viewModel.previewError {
                    Text(previewError)
                        .kluvsStyle(KluvsTheme.typography.body.medium)
                        .foregroundColor(KluvsTheme.colors.danger)
                }

                if let preview = viewModel.preview {
                    Text(preview.name)
                        .kluvsStyle(KluvsTheme.typography.headline.small)
                        .foregroundColor(KluvsTheme.colors.content)

                    if let joinError = viewModel.joinError {
                        Text(joinError)
                            .kluvsStyle(KluvsTheme.typography.body.medium)
                            .foregroundColor(KluvsTheme.colors.danger)
                    }

                    PrimaryButton(
                        text: viewModel.isJoining ? "Joining…" : "Join",
                        action: { viewModel.onJoinClicked() },
                        enabled: !viewModel.isJoining,
                        fillWidth: true
                    )
                }

                Spacer()
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(16)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(KluvsTheme.colors.background)
        .toolbar(.hidden, for: .navigationBar)
        .task {
            guard let initialToken, !initialToken.isEmpty else { return }
            viewModel.onTokenChanged(initialToken)
            viewModel.previewInvite()
        }
        .onChange(of: viewModel.joinedClubId) { _, newValue in
            if let clubId = newValue {
                onNavigateToClub(clubId)
                viewModel.onConsumeJoinedClubId()
            }
        }
        .onChange(of: viewModel.needsSignIn) { _, needsSignIn in
            if needsSignIn {
                onNeedsSignIn(viewModel.tokenInput.trimmingCharacters(in: .whitespaces))
                viewModel.onConsumeNeedsSignIn()
            }
        }
    }
}

#Preview {
    JoinView(onNavigateToClub: { _ in }, onNeedsSignIn: { _ in })
}
