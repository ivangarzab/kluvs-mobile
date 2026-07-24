import SwiftUI
import UIKit

/// Editable form-field primitive covering every real editable shape found across the app
/// (design-system "Inputs", see design-system/docs/inputs.md) — plain text, prefix/suffix-
/// decorated (e.g. "#" page number, "%" percentage), and multiline — as parameter combinations
/// on one component, not separate variant types. For a read-only field that opens a picker or
/// dialog instead of accepting keyboard input, see `PickerField`. Mirrors Android's `InputField`.
///
/// Deliberate platform-idiom deviation: Android/M3's `OutlinedTextField` uses a *floating* label
/// (sits inline at rest, animates above the border on focus/fill) — that's a Material convention
/// with no standard iOS equivalent. This uses a static label above the field instead, the
/// conventional iOS forms pattern (and what the app's own pre-existing `InputFieldView` already
/// did) — not an attempt at pixel-identical floating-label parity.
///
/// - Parameters:
///   - error: non-nil shows a red border/label and this text below the field (no "Error:" prefix
///     added here — callers supply the full message, matching real usage).
///   - helperText: muted hint text below the field, distinct from `error` — ignored if `error` is
///     also set.
public struct InputField: View {
    let label: String
    @Binding var value: String
    var prefix: String?
    var suffix: String?
    var error: String?
    var helperText: String?
    var singleLine: Bool
    var enabled: Bool
    var keyboardType: UIKeyboardType
    var submitLabel: SubmitLabel
    var onSubmit: () -> Void

    public init(
        label: String,
        value: Binding<String>,
        prefix: String? = nil,
        suffix: String? = nil,
        error: String? = nil,
        helperText: String? = nil,
        singleLine: Bool = true,
        enabled: Bool = true,
        keyboardType: UIKeyboardType = .default,
        submitLabel: SubmitLabel = .done,
        onSubmit: @escaping () -> Void = {}
    ) {
        self.label = label
        self._value = value
        self.prefix = prefix
        self.suffix = suffix
        self.error = error
        self.helperText = helperText
        self.singleLine = singleLine
        self.enabled = enabled
        self.keyboardType = keyboardType
        self.submitLabel = submitLabel
        self.onSubmit = onSubmit
    }

    private var borderColor: Color { error != nil ? KluvsTheme.colors.danger : KluvsTheme.colors.divider }
    private var labelColor: Color { error != nil ? KluvsTheme.colors.danger : KluvsTheme.colors.contentMuted }

    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .kluvsStyle(KluvsTheme.typography.caption)
                .foregroundColor(labelColor)

            HStack(alignment: .top, spacing: 4) {
                if let prefix {
                    Text(prefix)
                        .kluvsStyle(KluvsTheme.typography.body.medium)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                }

                Group {
                    if singleLine {
                        TextField("", text: $value)
                            .onSubmit(onSubmit)
                    } else {
                        TextField("", text: $value, axis: .vertical)
                            .lineLimit(3...6)
                    }
                }
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
                .tint(KluvsTheme.colors.accent)
                .keyboardType(keyboardType)
                .submitLabel(submitLabel)
                .disabled(!enabled)

                if let suffix {
                    Text(suffix)
                        .kluvsStyle(KluvsTheme.typography.body.medium)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(enabled ? KluvsTheme.colors.card : KluvsTheme.colors.background)
            .overlay(RoundedRectangle(cornerRadius: 8).strokeBorder(borderColor, lineWidth: 1))

            if let supporting = error ?? helperText {
                Text(supporting)
                    .kluvsStyle(KluvsTheme.typography.caption)
                    .foregroundColor(error != nil ? KluvsTheme.colors.danger : KluvsTheme.colors.contentMuted)
            }
        }
    }
}

#Preview {
    StatefulFieldPreview(["", "42", "70", "", "not-an-email", "disabled@kluvs.app"]) { values in
        VStack(spacing: 12) {
            InputField(label: "Email", value: values[0], helperText: "We'll never share this.", keyboardType: .emailAddress)
            InputField(label: "Page", value: values[1], prefix: "#", keyboardType: .numberPad)
            InputField(label: "Progress", value: values[2], suffix: "%", keyboardType: .numberPad)
            InputField(label: "Note", value: values[3], singleLine: false)
            InputField(label: "Email", value: values[4], error: "Enter a valid email address.")
            InputField(label: "Email", value: values[5], enabled: false)
        }
        .padding()
    }
}

/// Small helper so `#Preview` can host multiple independent `@State`-bound fields at once
/// (this codebase's established interactive-preview convention).
struct StatefulFieldPreview<Content: View>: View {
    @State private var values: [String]
    let content: ([Binding<String>]) -> Content

    init(_ initial: [String], @ViewBuilder content: @escaping ([Binding<String>]) -> Content) {
        self._values = State(initialValue: initial)
        self.content = content
    }

    var body: some View {
        content((0..<values.count).map { index in
            Binding(get: { values[index] }, set: { values[index] = $0 })
        })
    }
}
