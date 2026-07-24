import SwiftUI

/// Label-less filter-as-you-type field — e.g. filtering an already-visible list in a top app
/// bar. Distinct from `InputField`: no label, always shows a leading search icon (or a spinner
/// in its place while `isLoading`), and shows a trailing clear button whenever `value` isn't
/// empty. Distinct from the (separate, unbuilt) search-and-select combobox: this only filters
/// what's already on screen, it never triggers a network search or produces a "selected result"
/// state — see design-system/docs/inputs.md. Mirrors Android's `SearchField`.
///
/// Unlike Android, this doesn't need a hand-drawn-vs-wrapped-component workaround — SwiftUI's
/// plain `TextField` has no equivalent of M3's ~56dp enforced minimum height, so it's naturally
/// compact without needing `BasicTextField`-style manual decoration to get there.
public struct SearchField: View {
    @Binding var value: String
    var placeholder: String
    var enabled: Bool
    var isLoading: Bool

    @FocusState private var isFocused: Bool

    public init(
        value: Binding<String>,
        placeholder: String = "Search",
        enabled: Bool = true,
        isLoading: Bool = false
    ) {
        self._value = value
        self.placeholder = placeholder
        self.enabled = enabled
        self.isLoading = isLoading
    }

    private var accentColor: Color { isFocused ? KluvsTheme.colors.accent : KluvsTheme.colors.contentMuted }
    private var borderColor: Color { isFocused ? KluvsTheme.colors.accent : KluvsTheme.colors.divider }

    public var body: some View {
        HStack(spacing: 8) {
            if isLoading {
                ProgressView()
                    .controlSize(.mini)
                    .tint(accentColor)
                    .frame(width: 16, height: 16)
            } else {
                Icon(type: .search, contentDescription: nil, tint: accentColor)
                    .frame(width: 18, height: 18)
            }

            TextField(placeholder, text: $value)
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
                .tint(KluvsTheme.colors.accent)
                .submitLabel(.search)
                .focused($isFocused)
                .disabled(!enabled)

            if !value.isEmpty {
                Button(action: { value = "" }) {
                    Icon(type: .close, contentDescription: "Clear search", tint: accentColor)
                        .frame(width: 18, height: 18)
                }
                .disabled(!enabled)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(KluvsTheme.colors.card)
        .overlay(RoundedRectangle(cornerRadius: 8).strokeBorder(borderColor, lineWidth: 1))
        .animation(.easeInOut(duration: 0.15), value: isFocused)
    }
}

#Preview {
    VStack(spacing: 12) {
        StatefulFieldPreview([""]) { values in
            SearchField(value: values[0], placeholder: "Search books")
        }
        StatefulFieldPreview(["One Hundred Years"]) { values in
            SearchField(value: values[0], placeholder: "Search books")
        }
        StatefulFieldPreview(["Klara"]) { values in
            SearchField(value: values[0], placeholder: "Search books", isLoading: true)
        }
    }
    .padding()
}
