import SwiftUI

/// One action in an `ActionMenu` — a plain, single-line, optionally-destructive label. Not a
/// general list-item type; real usage across the app is uniformly 1-3 plain-text actions
/// (Edit/Delete/Share, Change Role/Remove, Edit/End Session) with no icons or subtext. Mirrors
/// Android's `ActionMenuItem`.
public struct ActionMenuItem {
    let label: String
    let action: () -> Void
    var isDestructive: Bool

    public init(label: String, action: @escaping () -> Void, isDestructive: Bool = false) {
        self.label = label
        self.action = action
        self.isDestructive = isDestructive
    }
}

/// DS-styled overflow menu — a "..." trigger opening a small popover list of `items`. Mirrors
/// Android's `ActionMenu`. Built on SwiftUI's native `Menu`, same as `Dropdown`.
///
/// Real platform limitation, not a shortcut taken here: SwiftUI/UIKit menus enforce their own
/// system typography for menu item text — a `Button` label inside `Menu` ignores `.font()`/
/// `.foregroundColor()` modifiers, so the Eyebrow family's IBM Plex Sans Medium + tracking can't
/// actually render inside a real menu the way Android's `DropdownMenuItem` (which fully supports
/// custom `Text` styling) does. `.uppercased()` still applies (that's just string data, not
/// styling), and `role: .destructive` still gets the system's own red tint automatically — but
/// full Eyebrow-style fidelity for the label text itself isn't achievable here.
public struct ActionMenu: View {
    let items: [ActionMenuItem]
    var icon: IconType
    var contentDescription: String?

    public init(items: [ActionMenuItem], icon: IconType = .moreVert, contentDescription: String? = "More options") {
        self.items = items
        self.icon = icon
        self.contentDescription = contentDescription
    }

    public var body: some View {
        Menu {
            ForEach(Array(items.enumerated()), id: \.offset) { _, item in
                Button(role: item.isDestructive ? .destructive : nil, action: item.action) {
                    Text(item.label.uppercased())
                }
            }
        } label: {
            Icon(type: icon, contentDescription: contentDescription, tint: KluvsTheme.colors.contentMuted)
                .frame(width: 24, height: 24)
                .frame(minWidth: 44, minHeight: 44)
                .contentShape(Rectangle())
        }
    }
}

#Preview("Short labels") {
    ActionMenu(items: [
        ActionMenuItem(label: "Share", action: {}),
        ActionMenuItem(label: "Edit", action: {}),
        ActionMenuItem(label: "Delete", action: {}, isDestructive: true),
    ])
    .padding()
}

#Preview("Longer labels") {
    ActionMenu(items: [
        ActionMenuItem(label: "Change Role", action: {}),
        ActionMenuItem(label: "Remove Member", action: {}, isDestructive: true),
    ])
    .padding()
}
