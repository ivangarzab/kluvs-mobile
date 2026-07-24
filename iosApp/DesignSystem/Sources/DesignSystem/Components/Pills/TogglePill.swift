import SwiftUI

/// Circular icon toggle (design-system "Pill" family), e.g. a like/favorite button — binary
/// checked/unchecked, not a multi-option selector, so unlike `ToggleControl<T>` there's no
/// generic option list here. Copper border/tint when `checked`, grey otherwise. Mirrors
/// Android's `TogglePill`.
public struct TogglePill: View {
    let checked: Bool
    let onToggle: () -> Void
    let iconChecked: IconType
    let iconUnchecked: IconType
    let contentDescription: String?
    var enabled: Bool

    public init(
        checked: Bool,
        onToggle: @escaping () -> Void,
        iconChecked: IconType,
        iconUnchecked: IconType,
        contentDescription: String?,
        enabled: Bool = true
    ) {
        self.checked = checked
        self.onToggle = onToggle
        self.iconChecked = iconChecked
        self.iconUnchecked = iconUnchecked
        self.contentDescription = contentDescription
        self.enabled = enabled
    }

    private var tint: Color { checked ? KluvsTheme.colors.accent : KluvsTheme.colors.contentMuted }
    private var borderColor: Color { checked ? KluvsTheme.colors.accent : KluvsTheme.colors.divider }

    public var body: some View {
        Button(action: onToggle) {
            Icon(type: checked ? iconChecked : iconUnchecked, contentDescription: contentDescription, tint: tint)
                .frame(width: 16, height: 16)
                .frame(width: 36, height: 36)
                .overlay(Circle().strokeBorder(borderColor, lineWidth: 1))
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    HStack(spacing: 12) {
        TogglePill(checked: false, onToggle: {}, iconChecked: .favorite, iconUnchecked: .favoriteOutline, contentDescription: "Like")
        TogglePill(checked: true, onToggle: {}, iconChecked: .favorite, iconUnchecked: .favoriteOutline, contentDescription: "Like", enabled: false)
    }
    .padding()
}
