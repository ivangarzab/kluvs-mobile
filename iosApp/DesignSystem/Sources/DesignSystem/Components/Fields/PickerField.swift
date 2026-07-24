import SwiftUI

/// Read-only field that opens a picker or dialog on tap instead of accepting keyboard input —
/// e.g. a date/time chooser. Hollow: `onTap` is entirely the caller's responsibility (present a
/// `DatePicker` sheet, whatever), and `value` is whatever already-formatted string the caller
/// wants displayed — this component has no idea what kind of picker it's fronting. Shares
/// `InputField`'s visual chrome (radius, border, label colors) but drops the raised input
/// background in favor of the surrounding surface, so it reads as inert rather than editable.
/// Mirrors Android's `PickerField`.
public struct PickerField: View {
    let label: String
    let value: String
    let onTap: () -> Void
    var error: String?
    var helperText: String?
    var enabled: Bool

    public init(
        label: String,
        value: String,
        onTap: @escaping () -> Void,
        error: String? = nil,
        helperText: String? = nil,
        enabled: Bool = true
    ) {
        self.label = label
        self.value = value
        self.onTap = onTap
        self.error = error
        self.helperText = helperText
        self.enabled = enabled
    }

    private var borderColor: Color { error != nil ? KluvsTheme.colors.danger : KluvsTheme.colors.divider }
    private var labelColor: Color { error != nil ? KluvsTheme.colors.danger : KluvsTheme.colors.contentMuted }

    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .kluvsStyle(KluvsTheme.typography.caption)
                .foregroundColor(labelColor)

            Text(value)
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .overlay(RoundedRectangle(cornerRadius: 8).strokeBorder(borderColor, lineWidth: 1))
                .contentShape(Rectangle())
                .onTapGesture(perform: onTap)

            if let supporting = error ?? helperText {
                Text(supporting)
                    .kluvsStyle(KluvsTheme.typography.caption)
                    .foregroundColor(error != nil ? KluvsTheme.colors.danger : KluvsTheme.colors.contentMuted)
            }
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
    }
}

#Preview {
    VStack(spacing: 12) {
        PickerField(label: "Date", value: "Jan 15, 2026", onTap: {})
        PickerField(label: "Date", value: "Jan 15, 2026", onTap: {}, enabled: false)
        PickerField(label: "Date", value: "", onTap: {}, error: "Pick a date.")
    }
    .padding()
}
