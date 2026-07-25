import SwiftUI
import Shared
import DesignSystem

private let assignableRoles: [Shared.Role] = [.admin, .member]

/// Change Role sheet body — presented via `.kluvsBottomSheet` at the call site (`ClubsView`), not
/// as its own `View` wrapping a `.sheet`. Scoped to role selection only, matching what this sheet
/// already did — web's `MemberModal` also folds in a per-session "reading" toggle and a combined
/// Add/Edit Member flow, but those aren't part of what this sheet does on mobile today (adding a
/// member here happens via invite link, not a name field) and the reading toggle needs a new
/// session-members PUT call not currently wired to this flow, so both are left out rather than
/// silently expanded.
struct ChangeRoleFields: View {
    let memberName: String
    @Binding var selectedRole: Shared.Role

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(memberName.uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .padding(.bottom, 8)

            ForEach(assignableRoles, id: \.ordinal) { role in
                HStack(spacing: 12) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(role.name.capitalized)
                            .kluvsStyle(KluvsTheme.typography.body.medium)
                            .foregroundColor(KluvsTheme.colors.content)
                        Text(role == .admin
                             ? "Can create and manage sessions and discussions"
                             : "Regular club member")
                            .kluvsStyle(KluvsTheme.typography.caption)
                            .foregroundColor(KluvsTheme.colors.contentMuted)
                    }
                    Spacer()
                    if selectedRole == role {
                        IconType.checkmark.image
                            .foregroundColor(KluvsTheme.colors.accent)
                            .fontWeight(.semibold)
                    }
                }
                .contentShape(Rectangle())
                .padding(.vertical, 10)
                .onTapGesture { selectedRole = role }
            }
        }
    }
}
