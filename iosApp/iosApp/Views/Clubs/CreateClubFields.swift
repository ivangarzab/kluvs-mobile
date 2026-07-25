import SwiftUI
import DesignSystem

/// Create Club sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`), not
/// as its own `View` wrapping a `.sheet`. Checked kluvs-frontend's AddClubModal.tsx as source of
/// truth: Name-only, matching what this sheet already did — web's Discord server/channel section
/// is the same deferred gap already noted on Edit Club (its own dedicated ticket). Web sets
/// founded_date to today automatically server-side on creation, so there's no field for it here
/// either way.
struct CreateClubFields: View {
    @Binding var name: String

    var body: some View {
        InputField(label: "Club Name", value: $name)
    }
}
