import SwiftUI
import Shared
import DesignSystem

/// Bottom sheet content for joining a club by invite code — the DS "fielded flow" home for
/// what used to be a full-screen push (`JoinView`, still kept registered for the not-yet-built
/// deep-link case; see its own doc comment). Mirrors Android's `JoinBottomSheetContent`.
struct JoinFields: View {
    @ObservedObject var viewModel: JoinViewModelWrapper

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            InputField(
                label: "Invite code",
                value: Binding(
                    get: { viewModel.tokenInput },
                    set: { viewModel.onTokenChanged($0) }
                )
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
            }

            if let joinError = viewModel.joinError {
                Text(joinError)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.danger)
            }
        }
    }
}

#Preview {
    JoinFields(viewModel: JoinViewModelWrapper())
        .padding()
        .background(KluvsTheme.colors.bar)
}
