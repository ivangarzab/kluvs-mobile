import SwiftUI
import Shared
import DesignSystem

struct MembersTab: View {
    let members: [Shared.MemberListItemInfo]
    var participants: [Shared.SessionParticipantInfo] = []
    var currentUserId: String = ""
    var userRole: Shared.Role? = nil
    var onChangeRole: (String) -> Void = { _ in }
    var onRemoveMember: (String) -> Void = { _ in }
    var onTransferOwnership: (String) -> Void = { _ in }
    var onInviteMember: () -> Void = {}

    private var isAdminOrAbove: Bool { userRole == .owner || userRole == .admin }
    private var isOwner: Bool { userRole == .owner }
    private var readingByMemberId: [String: Bool] {
        Dictionary(uniqueKeysWithValues: participants.map { ($0.memberId, $0.isReading) })
    }
    private var showEmptyState: Bool { members.count <= 1 && isAdminOrAbove }

    var body: some View {
        if members.isEmpty {
            NoTabData(text: String(localized: "empty_no_members"))
        } else {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    Text("\(members.count) members")
                        .kluvsStyle(KluvsTheme.typography.title.small, feature: true)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                    Spacer()
                    // The EmptyState below already carries its own "Invite Members" action, so
                    // this would just be a redundant second invite button.
                    if isAdminOrAbove && !showEmptyState {
                        OutlinedButton(text: "+ Invite", action: onInviteMember)
                    }
                }
                .padding(.bottom, 12)

                if showEmptyState {
                    // At most one member — nothing to scroll, so the row (if any) plus the
                    // EmptyState below just fill the tab directly. A ScrollView also proposes
                    // unbounded height to its content, so it can't be used here or the
                    // EmptyState gets stuck at its own small intrinsic size instead.
                    memberRows
                    EmptyState(
                        heading: "Just you, for now.",
                        body: "Invite a few people and this club starts to feel like one."
                    ) {
                        SecondaryButton(text: "Invite Members", action: onInviteMember)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    ScrollView {
                        memberRows
                    }
                }
            }
            .padding(16)
        }
    }

    private var memberRows: some View {
        ForEach(Array(members.enumerated()), id: \.element.memberId) { index, member in
            let isSelf = member.userId == currentUserId
            MemberListItem(
                member: member,
                isSelf: isSelf,
                isReading: readingByMemberId[member.memberId],
                showAdminActions: isAdminOrAbove && (!isSelf || isOwner) && member.role != .owner,
                showRemove: isOwner && !isSelf && member.role != .owner,
                showTransferOwnership: isOwner && !isSelf && member.role != .owner,
                onChangeRole: { onChangeRole(member.memberId) },
                onRemove: { onRemoveMember(member.memberId) },
                onTransferOwnership: { onTransferOwnership(member.memberId) }
            )

            if index < members.count - 1 {
                Divider()
            }
        }
    }
}

// MARK: - Member List Item

private struct MemberListItem: View {
    let member: Shared.MemberListItemInfo
    var isSelf: Bool = false
    var isReading: Bool? = nil
    var showAdminActions: Bool = false
    var showRemove: Bool = false
    var showTransferOwnership: Bool = false
    var onChangeRole: () -> Void = {}
    var onRemove: () -> Void = {}
    var onTransferOwnership: () -> Void = {}

    var body: some View {
        HStack(alignment: .center, spacing: 14) {
            Avatar(name: member.name, avatarUrl: member.avatarUrl, size: 40, memberId: member.memberId, isOwn: isSelf)

            // Identity column carries as much weight as the right rail's two tiers instead of
            // looking sparse next to it — matches Android's MemberListItem.
            VStack(alignment: .leading, spacing: 2) {
                HStack(spacing: 8) {
                    Text(member.name)
                        .kluvsStyle(KluvsTheme.typography.body.large)
                        .foregroundColor(KluvsTheme.colors.content)
                    if isSelf {
                        Text("YOU")
                            .kluvsStyle(KluvsTheme.typography.eyebrow)
                            .foregroundColor(KluvsTheme.colors.accent)
                    }
                }
                Text("@\(member.handle)")
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
            .padding(.vertical, 8)

            Spacer()

            VStack(alignment: .trailing, spacing: 0) {
                HStack(spacing: 8) {
                    if let isReading {
                        // 16pt icon inside 8pt padding = 32pt total box, matching Android's
                        // Modifier.size(32.dp).padding(8.dp) (fixed outer box, smaller visible icon).
                        Icon(
                            type: .reading,
                            contentDescription: isReading ? "Reading" : "Skipping",
                            tint: isReading ? KluvsTheme.colors.accent : KluvsTheme.colors.contentMuted
                        )
                        .frame(width: 16, height: 16)
                        .padding(8)
                    }
                    if showAdminActions || showRemove || showTransferOwnership {
                        ActionMenu(items: {
                            var items: [ActionMenuItem] = []
                            if showAdminActions {
                                items.append(ActionMenuItem(label: "Change Role", action: onChangeRole))
                            }
                            if showTransferOwnership {
                                items.append(ActionMenuItem(label: "Make Owner", action: onTransferOwnership))
                            }
                            if showRemove {
                                items.append(ActionMenuItem(label: "Remove", action: onRemove, isDestructive: true))
                            }
                            return items
                        }())
                    }
                }
                if member.role != .member {
                    RoleEyebrow(role: member.role)
                } else {
                    Spacer().frame(height: 16)
                }
            }
        }
        .padding(.vertical, 12)
    }
}

#Preview {
    MembersTab(members: [])
}
