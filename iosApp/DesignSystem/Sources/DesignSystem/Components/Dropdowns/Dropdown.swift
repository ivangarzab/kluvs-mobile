import SwiftUI

/// Pill-shaped value selector — a trigger showing the current selection (or `placeholder` when
/// none) that opens a menu of `options` on tap. Distinct from the Pill family
/// (`TriggerPill`/`TogglePill`) and from `ToggleControl`: those render every option inline, this
/// renders one value and reveals the rest in an overlay. Mirrors Android's `Dropdown<T>`.
///
/// Built on SwiftUI's native `Menu`, which owns its own open/closed presentation state — unlike
/// Android's version, there's no exposed `expanded` binding to animate a chevron rotation against
/// (a cosmetic difference, not a functional one; `Menu`'s own native disclosure animation covers
/// the same "this reveals more options" affordance).
///
/// - Parameter clearLabel: if non-nil, shows a leading option using this label that calls
///   `onSelect` with `nil` — e.g. "None" to unset the current selection. Omit to make a selection
///   mandatory once set.
public struct Dropdown<T: Equatable>: View {
    let options: [T]
    let selected: T?
    let onSelect: (T?) -> Void
    let label: (T) -> String
    let placeholder: String
    var clearLabel: String?
    var enabled: Bool

    public init(
        options: [T],
        selected: T?,
        onSelect: @escaping (T?) -> Void,
        label: @escaping (T) -> String,
        placeholder: String,
        clearLabel: String? = nil,
        enabled: Bool = true
    ) {
        self.options = options
        self.selected = selected
        self.onSelect = onSelect
        self.label = label
        self.placeholder = placeholder
        self.clearLabel = clearLabel
        self.enabled = enabled
    }

    private var tint: Color { selected != nil ? KluvsTheme.colors.accent : KluvsTheme.colors.contentMuted }
    private var borderColor: Color { selected != nil ? KluvsTheme.colors.accent : KluvsTheme.colors.divider }

    public var body: some View {
        Menu {
            if let clearLabel, selected != nil {
                Button(clearLabel) { onSelect(nil) }
            }
            ForEach(Array(options.enumerated()), id: \.offset) { _, option in
                Button {
                    onSelect(option)
                } label: {
                    if option == selected {
                        Label(label(option), systemImage: "checkmark")
                    } else {
                        Text(label(option))
                    }
                }
            }
        } label: {
            HStack(spacing: 8) {
                Text(selected.map(label) ?? placeholder)
                    .kluvsStyle(KluvsTheme.typography.label)
                    .foregroundColor(tint)
                Icon(type: .chevronDown, contentDescription: nil, tint: tint)
                    .frame(width: 14, height: 14)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .overlay(Capsule().strokeBorder(borderColor, lineWidth: 1))
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    HStack(spacing: 12) {
        Dropdown(
            options: ["Currently Reading", "Read", "Want to Read"],
            selected: nil,
            onSelect: { _ in },
            label: { $0 },
            placeholder: "Add to Shelf",
            clearLabel: "None"
        )
        Dropdown(
            options: ["Currently Reading", "Read", "Want to Read"],
            selected: "Read",
            onSelect: { _ in },
            label: { $0 },
            placeholder: "Add to Shelf",
            enabled: false
        )
    }
    .padding()
}
