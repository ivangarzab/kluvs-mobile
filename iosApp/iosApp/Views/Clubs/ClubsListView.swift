import SwiftUI
import Shared
import DesignSystem

/// Entry-point list of the member's clubs — mirrors web's `/clubs` page / Android's
/// `ClubsListScreen`. Tapping a row navigates into the club detail screen. The FAB
/// opens `CreateClubSheet`.
struct ClubsListView: View {
    let clubs: [Shared.ClubListItem]
    let onClubSelected: (String) -> Void
    let onAddClub: () -> Void
    var isRefreshing: Bool = false
    var onRefresh: () -> Void = {}

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            Color.kluvsBackground.ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    ForEach(clubs, id: \.id) { club in
                        Button(action: { onClubSelected(club.id) }) {
                            ClubListRow(club: club)
                        }
                        .buttonStyle(.plain)
                        Divider()
                    }
                }
            }
            .background(Color.kluvsBackground)
            .kluvsPullToRefresh(isRefreshing: isRefreshing, onRefresh: onRefresh)

            ClubsFAB(action: onAddClub)
        }
    }
}

/// The "+ New club" FAB — shared between the populated list and the empty state (both need it;
/// only the loading/error states don't).
struct ClubsFAB: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "plus")
                .font(.system(size: 20, weight: .semibold))
                .foregroundColor(.brandOnPrimary)
                .frame(width: 56, height: 56)
                .background(Color.brandOrange)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .shadow(radius: 4)
        }
        .padding(16)
    }
}

private struct ClubListRow: View {
    let club: Shared.ClubListItem

    var body: some View {
        HStack(spacing: 12) {
            BookCoverImage(imageUrl: club.bookCoverUrl, width: 40)

            VStack(alignment: .leading, spacing: 6) {
                HStack(spacing: 8) {
                    Text(club.name)
                        .kluvsStyle(KluvsTheme.typography.title.medium)
                        .foregroundColor(KluvsTheme.colors.content)
                    if let role = club.role {
                        RoleEyebrow(role: role)
                    }
                }

                if let bookTitle = club.bookTitle {
                    Text(bookTitle)
                        .kluvsStyle(KluvsTheme.typography.title.small, feature: true)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                }

                if !club.memberAvatarUrls.isEmpty {
                    AvatarStack(
                        members: club.memberAvatarUrls.map {
                            AvatarStackMember(id: $0.memberId, name: $0.name, avatarUrl: $0.avatarUrl)
                        },
                        size: 20
                    )
                }
            }

            Spacer()

            Image(systemName: "chevron.right")
                .foregroundColor(KluvsTheme.colors.contentMuted)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 16)
        .contentShape(Rectangle())
    }
}

#Preview {
    ClubsListView(
        clubs: [],
        onClubSelected: { _ in },
        onAddClub: {}
    )
}
