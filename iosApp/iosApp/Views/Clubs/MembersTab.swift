import SwiftUI
import Shared

struct MembersTab: View {
    let members: [Shared.MemberListItemInfo]

    var body: some View {
        ScrollView {
            if members.isEmpty {
                NoTabData(text: String(localized: "empty_no_members"))
            } else {
                VStack(alignment: .leading, spacing: 0) {
                    Text(String(format: NSLocalizedString("label_members_section", comment: ""), Int32(members.count)))
                        .font(.headline)
                        .padding(8)

                    ForEach(Array(members.enumerated()), id: \.offset) { index, member in
                        MemberListItem(member: member)

                        if index < members.count - 1 {
                            Divider()
                                .padding(.vertical, 4)
                        }
                    }
                }
                .padding()
            }
        }
    }
}

// MARK: - Member List Item
struct MemberListItem: View {
    let member: Shared.MemberListItemInfo

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            // Member avatar
            MemberAvatar(
                avatarUrl: member.avatarUrl,
                size: 40
            )

            VStack(alignment: .leading, spacing: 2) {
                Text(member.name)
                    .font(.body)
                    .fontWeight(.medium)

                Text(member.handle)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()
        }
        .padding(.vertical, 12)
    }
}

#Preview {
    MembersTab(members: [])
}
