import SwiftUI
import DesignSystem

/// Edit Club sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`), not as
/// its own `View` wrapping a `.sheet`. Mirrors web's `EditClubModal`: Name field + an embedded
/// Danger Zone ("Delete club…") that hands off to the existing top-level delete confirmation,
/// matching web's `onDeleteClub` callback closing this sheet and opening `DeleteClubModal`
/// separately.
///
/// Web's Discord server/channel section and Founded Date field are intentionally left out —
/// Discord linking has its own dedicated ticket, and Founded Date editing needs new shared KMP
/// layer support (`ClubDetailsViewModel` only exposes a derived, display-only `foundedYear` today)
/// rather than being a pure UI-shell change, so both are deferred.
struct EditClubFields: View {
    let currentName: String
    @Binding var name: String
    let onDeleteClub: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            InputField(label: "Club Name", value: $name)
            DangerZoneBox(actionLabel: "Delete club…", onActionTap: onDeleteClub)
        }
    }
}

#Preview {
    EditClubFields(currentName: "My Book Club", name: .constant("My Book Club"), onDeleteClub: {})
        .padding()
        .background(KluvsTheme.colors.bar)
}
