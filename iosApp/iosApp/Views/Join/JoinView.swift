import SwiftUI
import Shared
import DesignSystem

/// Join-by-invite-token screen. Not reachable from any UI action today — "Join with a code"
/// inside the (already-authenticated) Clubs tab opens `JoinFields` in a local bottom sheet
/// instead. This full screen is kept registered (`MainRoute.join`) for the not-yet-built iOS
/// Universal Links deep-link case — tapping a raw invite URL while signed out, which needs to
/// land somewhere before auth resolves and needs the `onNeedsSignIn` handoff below, neither of
/// which a nested bottom sheet can do. Mirrors Android's `JoinScreen`.
///
/// The preview shows only the club name — `Shared.ClubPreview` has no avatar/member-count yet
/// (also a follow-up, needs a backend spec change).
struct JoinView: View {
    let onNavigateToClub: (String) -> Void
    let onNeedsSignIn: (String) -> Void

    @StateObject private var viewModel = JoinViewModelWrapper()
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            IconButton(type: .back, contentDescription: "Back", action: { dismiss() })

            Text("Join a club")
                .kluvsStyle(KluvsTheme.typography.headline.small)
                .foregroundColor(KluvsTheme.colors.content)

            InputField(
                label: "Invite code",
                value: Binding(
                    get: { viewModel.tokenInput },
                    set: { viewModel.onTokenChanged($0) }
                )
            )

            SecondaryButton(
                text: "Preview",
                action: { viewModel.previewInvite() },
                enabled: !viewModel.tokenInput.trimmingCharacters(in: .whitespaces).isEmpty && !viewModel.isLoadingPreview
            )
            .frame(maxWidth: .infinity)

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
                    enabled: !viewModel.isJoining
                )
                .frame(maxWidth: .infinity)
            }

            Spacer()
        }
        .padding(16)
        .background(KluvsTheme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
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
