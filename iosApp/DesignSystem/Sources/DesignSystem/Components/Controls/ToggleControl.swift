import SwiftUI

/// Filled two-or-more-way toggle (design-system "Segmented Control", filled/Track-By variant —
/// see `.kluvs-segmented` in colors_and_type.css). A single pill-shaped container with a hairline
/// divider between segments (none before the first); the selected segment fills solid `accent`
/// with `onAccent` text. Mirrors Android's `ToggleControl<T>`.
public struct ToggleControl<T: Equatable>: View {
    let options: [T]
    let selected: T
    let onSelect: (T) -> Void
    let label: (T) -> String

    public init(options: [T], selected: T, onSelect: @escaping (T) -> Void, label: @escaping (T) -> String) {
        self.options = options
        self.selected = selected
        self.onSelect = onSelect
        self.label = label
    }

    public var body: some View {
        HStack(spacing: 0) {
            ForEach(Array(options.enumerated()), id: \.offset) { index, option in
                let isSelected = option == selected
                Button(action: { onSelect(option) }) {
                    Text(label(option))
                        .kluvsStyle(KluvsTheme.typography.label)
                        .foregroundColor(isSelected ? KluvsTheme.colors.onAccent : KluvsTheme.colors.contentMuted)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(isSelected ? KluvsTheme.colors.accent : Color.clear)
                }
                .overlay(alignment: .leading) {
                    if index != 0 {
                        Rectangle().fill(KluvsTheme.colors.divider).frame(width: 1)
                    }
                }
            }
        }
        .clipShape(Capsule())
        .overlay(Capsule().strokeBorder(KluvsTheme.colors.divider, lineWidth: 1))
    }
}

#Preview {
    StatefulPreviewWrapper("Page") { selected in
        ToggleControl(options: ["Page", "Percent"], selected: selected.wrappedValue, onSelect: { selected.wrappedValue = $0 }, label: { $0 })
    }
    .padding()
}

/// Small helper so `#Preview` can host `@State`-driven interactive previews (this codebase's
/// established convention — previews double as click-through implementation examples, not just
/// static snapshots).
private struct StatefulPreviewWrapper<Value, Content: View>: View {
    @State private var value: Value
    let content: (Binding<Value>) -> Content

    init(_ initial: Value, @ViewBuilder content: @escaping (Binding<Value>) -> Content) {
        self._value = State(initialValue: initial)
        self.content = content
    }

    var body: some View {
        content($value)
    }
}
