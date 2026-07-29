import SwiftUI

/// A tappable icon — nothing more, no container/border/background, unlike the rest of the
/// button family. Mirrors Android's `IconButton`.
///
/// Minimum tap target is 44x44pt (iOS HIG), not Android's 48dp — a real, deliberate platform
/// difference, not a mismatch to reconcile.
public struct IconButton: View {
    let type: IconType
    let contentDescription: String?
    let action: () -> Void
    var enabled: Bool
    var tint: Color?

    public init(
        type: IconType,
        contentDescription: String?,
        action: @escaping () -> Void,
        enabled: Bool = true,
        tint: Color? = nil
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
                .frame(width: 24, height: 24)
                .frame(minWidth: 44, minHeight: 44)
                .contentShape(Rectangle())
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    HStack(spacing: 8) {
        IconButton(type: .arrowBack, contentDescription: "Back", action: {})
        IconButton(type: .moreVert, contentDescription: "More options", action: {})
        IconButton(type: .moreVert, contentDescription: "More options", action: {}, enabled: false)
    }
    .padding()
}
