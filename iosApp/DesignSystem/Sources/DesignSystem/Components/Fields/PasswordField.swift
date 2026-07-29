import SwiftUI

/// Always-masked password variant of `InputField` — a dedicated component, not a boolean flag on
/// `InputField`, matching this codebase's established convention of separate named components
/// for a categorically different input mode (see `InputField` vs `PickerField`). Permanently
/// masked, no reveal toggle — matches neither Android's `PasswordField` nor any real reveal-
/// toggle pattern anywhere in this app (checked: web's password fields are plain, no eye icon
/// either).
///
/// Mirrors Android's `PasswordField`, added there to fix a real regression: an earlier migration
/// off the app's old hand-rolled input component silently dropped password masking. Built fresh
/// here rather than risk the same gap - iOS's own pre-existing `InputFieldView` already had a
/// reveal-toggle option Android's real component doesn't, deliberately not carried over (see the
/// permanent-masking note above).
public struct PasswordField: View {
    let label: String
    @Binding var value: String
    var error: String?
    var helperText: String?
    var enabled: Bool
    var submitLabel: SubmitLabel
    var onSubmit: () -> Void

    public init(
        label: String,
        value: Binding<String>,
        error: String? = nil,
        helperText: String? = nil,
        enabled: Bool = true,
        submitLabel: SubmitLabel = .done,
        onSubmit: @escaping () -> Void = {}
    ) {
        self.label = label
        self._value = value
        self.error = error
        self.helperText = helperText
        self.enabled = enabled
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

            SecureField("", text: $value)
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
                .tint(KluvsTheme.colors.accent)
                .submitLabel(submitLabel)
                .onSubmit(onSubmit)
                .disabled(!enabled)
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
    StatefulFieldPreview(["", ""]) { values in
        VStack(spacing: 12) {
            PasswordField(label: "Password", value: values[0], helperText: "At least 8 characters.")
            PasswordField(label: "Password", value: values[1], error: "Passwords don't match.")
        }
        .padding()
    }
}
