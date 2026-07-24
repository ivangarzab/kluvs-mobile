import SwiftUI

/// Circular, generated member avatar: shows an uploaded image if present, otherwise
/// initials on a deterministic hue background. Mirrors web's `Avatar` / Android's `Avatar`.
///
/// Role is never shown here — no ring, no badge. Use `RoleEyebrow` for role display.
///
/// Not yet ported: Android's `isLoading` param (semi-transparent overlay + spinner) — depends on
/// `LoadingSpinner`, which doesn't exist on this platform yet. Real, flagged gap, not silently
/// dropped.
public struct Avatar: View {
    let name: String
    let avatarUrl: String?
    let size: CGFloat
    var memberId: String
    var isOwn: Bool

    public init(name: String, avatarUrl: String?, size: CGFloat, memberId: String = "", isOwn: Bool = false) {
        self.name = name
        self.avatarUrl = avatarUrl
        self.size = size
        self.memberId = memberId
        self.isOwn = isOwn
    }

    @Environment(\.colorScheme) private var colorScheme

    private var hues: [Color] { colorScheme == .dark ? Color.avatarHuesDark : Color.avatarHuesLight }

    private var backgroundColor: Color {
        isOwn ? .brandOrange : hues[Self.hueIndex(memberId, count: hues.count)]
    }

    private var textColor: Color {
        if isOwn { return .brandOnPrimary }
        return colorScheme == .dark ? .foregroundWarmPrimary : .foregroundLightLabelVariant
    }

    public var body: some View {
        ZStack {
            if let urlString = avatarUrl, !urlString.isEmpty, let url = URL(string: urlString) {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .success(let image):
                        image.resizable().scaledToFill()
                    default:
                        initialsView
                    }
                }
            } else {
                initialsView
            }
        }
        .frame(width: size, height: size)
        .clipShape(Circle())
    }

    private var initialsView: some View {
        ZStack {
            backgroundColor
            Text(Self.initials(of: name))
                .font(.ebGaramondMedium(size: size * 0.3))
                .foregroundColor(textColor)
        }
    }

    private static func hueIndex(_ memberId: String, count: Int) -> Int {
        memberId.isEmpty ? 0 : abs(memberId.hashValue) % count
    }

    private static func initials(of name: String) -> String {
        let parts = name.trimmingCharacters(in: .whitespaces)
            .split(separator: " ")
            .filter { !$0.isEmpty }
        if parts.count >= 2 {
            return "\(parts.first!.prefix(1))\(parts.last!.prefix(1))".uppercased()
        } else if let first = parts.first {
            return String(first.prefix(2)).uppercased()
        }
        return "?"
    }
}

#Preview {
    Avatar(name: "Jane Doe", avatarUrl: nil, size: 60, memberId: "42")
        .padding()
}
