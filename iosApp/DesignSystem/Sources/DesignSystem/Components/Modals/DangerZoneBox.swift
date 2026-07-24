import SwiftUI

/// Secondary destructive action tucked inside an edit sheet's body (e.g. "Delete club" inside
/// Edit Club) — design-system "Danger zone box" (see design-system/docs/modal.md). Sits at the
/// bottom of `BottomSheet`'s body content, never as its own footer button. The box carries the
/// danger signal (subtle red tint); the button inside stays quiet (muted `OutlinedButton`) —
/// tapping it is expected to open a `ConfirmationDialog`, not act immediately. Mirrors Android's
/// `DangerZoneBox`.
public struct DangerZoneBox: View {
    let actionLabel: String
    let onActionTap: () -> Void

    public init(actionLabel: String, onActionTap: @escaping () -> Void) {
        self.actionLabel = actionLabel
        self.onActionTap = onActionTap
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("DANGER ZONE")
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(KluvsTheme.colors.contentMuted)
            SecondaryButton(text: actionLabel, action: onActionTap, buttonColor: KluvsTheme.colors.danger)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(KluvsTheme.colors.dangerSubtle)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .strokeBorder(KluvsTheme.colors.danger.opacity(0.2), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

#Preview {
    DangerZoneBox(actionLabel: "Delete club", onActionTap: {})
        .padding()
        .background(KluvsTheme.colors.bar)
}
