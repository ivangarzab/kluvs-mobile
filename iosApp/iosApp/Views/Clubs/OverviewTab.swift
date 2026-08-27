import SwiftUI
import Shared
import DesignSystem

/// Overview tab: active-session summary (book, participation, own progress) and an
/// "up next" discussion teaser. Mirrors web's mobile Overview tab / Android's `OverviewTab`.
/// The club masthead (name, meta row) lives above the tab picker in `ClubsView`, not here.
/// The full discussion timeline and end-session flow stay on the Discussions tab.
struct OverviewTab: View {
    let clubDetails: Shared.ClubDetails?
    let sessionDetails: Shared.ActiveSessionDetails?
    var ownProgress: Shared.OwnProgressInfo? = nil
    var userRole: Shared.Role? = nil
    var members: [Shared.MemberListItemInfo] = []
    var currentMemberId: String? = nil
    var onEditSession: () -> Void = {}
    var onEndSession: () -> Void = {}
    var onUpdateProgress: () -> Void = {}
    var onCreateSession: () -> Void = {}
    var onToggleParticipation: (Bool) -> Void = { _ in }

    private var isAdminOrAbove: Bool { userRole == .owner || userRole == .admin }

    var body: some View {
        Group {
            if clubDetails == nil {
                NoTabData(text: String(localized: "empty_no_club_details"))
            } else if let session = sessionDetails {
                // Only the actual session content scrolls — a ScrollView also proposes
                // unbounded height to its content, so noActiveSessionState below is kept
                // outside of it entirely, otherwise it can't fill the tab either.
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        sessionSummary(session: session)

                        if let next = session.discussions.first(where: { $0.isNext }) {
                            Divider()
                            upNextTeaser(discussion: next)
                            Divider()
                        }
                    }
                    .padding(16)
                }
            } else {
                noActiveSessionState
            }
        }
    }

    // MARK: - Session Summary

    private var readingParticipants: [Shared.SessionParticipantInfo] {
        (sessionDetails?.participants ?? []).filter { $0.isReading }
    }

    private var readingMembers: [AvatarStackMember] {
        let matched: [Shared.MemberListItemInfo] = readingParticipants.compactMap { participant in
            members.first { $0.memberId == participant.memberId }
        }
        return matched.map { AvatarStackMember(id: $0.memberId, name: $0.name, avatarUrl: $0.avatarUrl) }
    }

    private func isOwnReading(session: Shared.ActiveSessionDetails) -> Bool {
        guard let currentMemberId else { return false }
        return session.participants.contains { $0.memberId == currentMemberId && $0.isReading }
    }

    private var totalMemberCount: Int {
        Int(clubDetails?.memberCount ?? Int32(members.count))
    }

    @ViewBuilder
    private func sessionSummary(session: Shared.ActiveSessionDetails) -> some View {
        let ownReading = isOwnReading(session: session)
        let canToggle = currentMemberId != nil

        VStack(alignment: .leading, spacing: 12) {
            sessionHeader(session: session)
            participationRow(ownReading: ownReading, canToggle: canToggle)

            if ownReading {
                OwnProgressRow(
                    percent: ownProgress.map { Int($0.percent) },
                    statusLabel: ownProgress?.label,
                    onUpdateProgress: onUpdateProgress,
                    leftLabel: "\(session.discussions.filter { $0.isPast }.count) of \(session.discussions.count) discussions",
                    leftLabelEmphasized: true
                )
            }
        }
    }

    private func sessionHeader(session: Shared.ActiveSessionDetails) -> some View {
        HStack(alignment: .top, spacing: 16) {
            BookCoverImage(imageUrl: session.book.imageUrl, width: 80)

            VStack(alignment: .leading, spacing: 2) {
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("ACTIVE SESSION")
                            .kluvsStyle(KluvsTheme.typography.eyebrow)
                            .foregroundColor(KluvsTheme.colors.accent)

                        Text(session.book.title)
                            .kluvsStyle(KluvsTheme.typography.title.large, feature: true)
                            .foregroundColor(KluvsTheme.colors.content)
                    }
                    Spacer()
                    if isAdminOrAbove {
                        sessionOverflowMenu
                    }
                }
                Text(session.book.author)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
        }
    }

    @ViewBuilder
    private func participationRow(ownReading: Bool, canToggle: Bool) -> some View {
        HStack {
            if !readingMembers.isEmpty {
                HStack(spacing: 8) {
                    AvatarStack(members: readingMembers, size: 24)
                    Text("\(readingParticipants.count) of \(totalMemberCount) reading")
                        .kluvsStyle(KluvsTheme.typography.caption)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                }
            } else {
                Text("No participants yet")
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }

            Spacer()

            if canToggle {
                OutlinedButton(
                    text: ownReading ? "Opt out" : "Join this Read",
                    action: { onToggleParticipation(!ownReading) }
                )
            }
        }
    }

    private var sessionOverflowMenu: some View {
        ActionMenu(items: [
            ActionMenuItem(label: "Edit Session", action: onEditSession),
            ActionMenuItem(label: "End Session", action: onEndSession, isDestructive: true),
        ])
    }

    // MARK: - No Active Session

    private var noActiveSessionState: some View {
        EmptyState(
            heading: "No active session yet.",
            body: "Start a session to begin reading together."
        ) {
            if isAdminOrAbove {
                // Primary, not Secondary like other EmptyState actions — starting a session
                // is the single most important next step for a brand-new club.
                PrimaryButton(text: "Start Session", action: onCreateSession)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    // MARK: - Up Next

    private func upNextTeaser(discussion: Shared.DiscussionTimelineItemInfo) -> some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 4) {
                Text("UP NEXT")
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.accent)
                Text(discussion.title)
                    .kluvsStyle(KluvsTheme.typography.title.medium)
                    .foregroundColor(KluvsTheme.colors.content)
                Text(discussion.location)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
            }
            Spacer()
            Text(discussion.date)
                .kluvsStyle(KluvsTheme.typography.caption)
                .foregroundColor(KluvsTheme.colors.accent)
        }
    }
}

#Preview {
    OverviewTab(clubDetails: nil, sessionDetails: nil)
}
