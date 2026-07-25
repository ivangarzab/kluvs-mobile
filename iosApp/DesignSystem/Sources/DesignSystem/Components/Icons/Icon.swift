import SwiftUI

/// A single, tinted icon — mirrors Android's `Icon(type:contentDescription:tint:)`.
/// `contentDescription` maps to SwiftUI's accessibility label; passing `nil` marks the icon as
/// decorative (hidden from VoiceOver), matching Android's own `contentDescription = null`
/// convention for purely-decorative icons (e.g. inside a button that already has its own label).
///
/// No default size is set — same as Android's own `Icon`, which relies entirely on a caller-
/// supplied `Modifier.size(...)`. Always wrap a call in an explicit `.frame(width:height:)`;
/// standard SwiftUI practice for any `.resizable()` image, not a Kluvs-specific requirement.
public struct Icon: View {
    public let type: IconType
    public let contentDescription: String?
    public var tint: Color?

    public init(type: IconType, contentDescription: String?, tint: Color? = nil) {
        self.type = type
        self.contentDescription = contentDescription
        self.tint = tint
    }

    public var body: some View {
        // `.resizable()` internally so a caller can size this the same flexible way Android's
        // `Icon(modifier = Modifier.size(...))` allows — plain `Image` only exposes `.resizable()`
        // on itself, not on the opaque `some View` this computed property would otherwise return,
        // which would leave every caller stuck at the icon's native intrinsic size.
        Group {
            if let tint {
                type.image.renderingMode(.template).resizable().scaledToFit().foregroundColor(tint)
            } else {
                type.image.renderingMode(.template).resizable().scaledToFit()
            }
        }
        .accessibilityHidden(contentDescription == nil)
        .accessibilityLabel(contentDescription.map(Text.init) ?? Text(""))
    }
}

#Preview {
    HStack(spacing: 12) {
        Icon(type: .arrowBack, contentDescription: "Back")
            .frame(width: 24, height: 24)
        Icon(type: .moreVert, contentDescription: "More options")
            .frame(width: 24, height: 24)
        Icon(type: .favorite, contentDescription: nil, tint: .brandOrange)
            .frame(width: 24, height: 24)
        Icon(type: .club, contentDescription: nil)
            .frame(width: 24, height: 24)
    }
    .padding()
}
