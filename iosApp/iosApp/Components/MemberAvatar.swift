import SwiftUI
import Shared
import DesignSystem

/**
 * Displays a member's avatar image with fallback to initials on the brand copper background
 * (mirrors Android's `Avatar(isOwn = true)` — the "own profile" case never uses the generic
 * hue palette, matching web/Android).
 *
 * Shows a colored rim and role icon overlay for OWNER (gold crown) and ADMIN (blue shield).
 * - Crown sits at the top edge of the avatar
 * - Shield sits at the bottom edge of the avatar
 */
struct MemberAvatar: View {
    let avatarUrl: String?
    let size: CGFloat
    var name: String = ""
    var role: Role? = nil
    var isLoading: Bool = false
    var onClick: (() -> Void)? = nil

    private var rimColor: Color? {
        guard let role = role else { return nil }
        switch role {
        case .owner: return Color(red: 0xEF/255.0, green: 0xBF/255.0, blue: 0x04/255.0) // gold
        case .admin: return Color(red: 0x00/255.0, green: 0x67/255.0, blue: 0x81/255.0) // blue
        default: return nil
        }
    }

    private var roleIcon: IconType? {
        guard let role = role else { return nil }
        switch role {
        case .owner: return .crown
        case .admin: return .shield
        default: return nil
        }
    }

    var body: some View {
        let iconSize = size * 0.28
        // Offset from ZStack center to align icon at top or bottom edge, then shift outward by 5pt
        let iconYOffset: CGFloat = {
            guard let role = role else { return 0 }
            let edgeFromCenter = size / 2 - iconSize / 2
            switch role {
            case .owner: return -(edgeFromCenter + 5)
            case .admin: return  (edgeFromCenter + 5)
            default: return 0
            }
        }()

        ZStack {
            // Avatar with optional rim
            avatarView

            // Role icon overlay (no background — bare tinted icon)
            if let roleIcon, let color = rimColor {
                roleIcon.image
                    .resizable()
                    .scaledToFit()
                    .frame(width: iconSize, height: iconSize)
                    .foregroundColor(color)
                    .offset(y: iconYOffset)
            }
        }
        .padding(4)
        .frame(width: size + 8, height: size + 8) // account for 4pt padding on each side
        .onTapGesture {
            onClick?()
        }
    }

    @ViewBuilder
    private var avatarView: some View {
        // isOwn: true forces the copper "own user" background for the initials fallback,
        // matching Android's Avatar(isOwn = true) used at both MemberAvatar call sites (Me's
        // profile section, Settings' edit-profile avatar) — never the generic hue palette.
        Avatar(name: name, avatarUrl: avatarUrl, size: size, isOwn: true, isLoading: isLoading)
            .overlay(
                Circle()
                    .strokeBorder(rimColor ?? Color.clear, lineWidth: 2)
            )
    }
}

#Preview {
    VStack(spacing: 20) {
        HStack(spacing: 16) {
            MemberAvatar(avatarUrl: nil, size: 60, name: "Jane Doe", role: .member)
            MemberAvatar(avatarUrl: nil, size: 60, name: "Jane Doe", role: .admin)
            MemberAvatar(avatarUrl: nil, size: 60, name: "Jane Doe", role: .owner)
        }
        MemberAvatar(avatarUrl: nil, size: 60, name: "Jane Doe", isLoading: true)
    }
    .padding()
}
