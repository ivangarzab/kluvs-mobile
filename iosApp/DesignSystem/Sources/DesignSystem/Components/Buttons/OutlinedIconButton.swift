import SwiftUI

/// Icon-only button with a bordered container — the icon-only counterpart to `OutlinedButton`'s
/// grey/muted outline, sharing its 12pt radius. Mirrors Android's `OutlinedIconButton`.
public struct OutlinedIconButton: View {
    let type: IconType
    let contentDescription: String?
    let action: () -> Void
    var enabled: Bool
    var tint: Color

    public init(
        type: IconType,
        contentDescription: String?,
        action: @escaping () -> Void,
        enabled: Bool = true,
        tint: Color = KluvsTheme.colors.contentMuted
    ) {
        self.type = type
        self.contentDescription = contentDescription
        self.action = action
        self.enabled = enabled
        self.tint = tint
    }

    public var body: some View {
        Button(action: action) {
            Icon(type: type, contentDescription: contentDescription, tint: tint)
                .frame(width: 20, height: 20)
                .frame(width: 40, height: 40)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(KluvsTheme.colors.divider, lineWidth: 1)
                )
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    HStack(spacing: 12) {
        OutlinedIconButton(type: .search, contentDescription: "Search", action: {})
        OutlinedIconButton(type: .search, contentDescription: "Search", action: {}, enabled: false)
    }
    .padding()
}
