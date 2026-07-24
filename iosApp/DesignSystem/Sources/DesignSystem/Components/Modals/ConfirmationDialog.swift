import SwiftUI

/// Centered confirm/cancel dialog — design-system "Modal", centered-dialog form (see
/// design-system/docs/modal.md). Reserved for hard, confirm-only confirmations (Delete Club, Sign
/// Out, Remove Member) — anything with fields to fill in belongs in `BottomSheet` instead.
/// Mirrors Android's `ConfirmationDialog`.
///
/// Built as a fully custom overlay, not SwiftUI's native `.alert()` — unlike Android's M3
/// `AlertDialog` (which supports arbitrary custom styling in production, just not in Compose's
/// static Preview), iOS's `.alert()` cannot be restyled at all: no custom background color, no
/// custom typography, system chrome only. Present via the `.kluvsConfirmationDialog(...)` view
/// modifier below, not by constructing this type directly.
///
/// - Parameter isDestructive: true for irreversible actions (delete/remove) — tints the title and
///   confirm label danger-red instead of copper. Sign-out-style confirmations (reversible, just
///   disruptive) should pass false.
/// - Parameter dismissLabel: nil renders a single-button dialog (e.g. informational/error alerts
///   that have nothing to cancel) — the confirm button becomes the sole, trailing-aligned action.
struct ConfirmationDialog: View {
    let title: String
    let message: String
    let onConfirm: () -> Void
    let onDismiss: () -> Void
    var confirmLabel: String = "Confirm"
    var dismissLabel: String? = "Cancel"
    var isDestructive: Bool = false

    private var accentColor: Color { isDestructive ? KluvsTheme.colors.danger : KluvsTheme.colors.accent }

    var body: some View {
        VStack(spacing: 0) {
            Text(title.uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(accentColor)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)
                .padding(.top, 20)
                .padding(.bottom, 20)

            Rectangle().fill(KluvsTheme.colors.divider).frame(height: 1)

            Text(message)
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(24)

            Rectangle().fill(KluvsTheme.colors.divider).frame(height: 1)

            HStack {
                if let dismissLabel {
                    Button(dismissLabel, action: onDismiss)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                    Spacer()
                } else {
                    Spacer()
                }
                Button(confirmLabel, action: onConfirm)
                    .foregroundColor(accentColor)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
        .background(KluvsTheme.colors.bar)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .frame(maxWidth: 400)
    }
}

public extension View {
    /// Presents a `ConfirmationDialog` as a custom scrim + card overlay when `isPresented` is
    /// true. `onConfirm` and dismissal (confirm, cancel, or tapping the scrim) all set
    /// `isPresented` back to false automatically — callers only need to react to `onConfirm`.
    ///
    /// `onDismiss` is optional and fires on cancel/scrim-tap only (not on confirm) — needed when
    /// `isPresented` mirrors state owned elsewhere (e.g. a shared KMP view model) that must be
    /// told explicitly the dialog was dismissed without confirming, not just flipped locally.
    func kluvsConfirmationDialog(
        isPresented: Binding<Bool>,
        title: String,
        message: String,
        confirmLabel: String = "Confirm",
        dismissLabel: String? = "Cancel",
        isDestructive: Bool = false,
        onDismiss: (() -> Void)? = nil,
        onConfirm: @escaping () -> Void
    ) -> some View {
        overlay {
            if isPresented.wrappedValue {
                ZStack {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                        .onTapGesture {
                            isPresented.wrappedValue = false
                            onDismiss?()
                        }
                    ConfirmationDialog(
                        title: title,
                        message: message,
                        onConfirm: {
                            isPresented.wrappedValue = false
                            onConfirm()
                        },
                        onDismiss: {
                            isPresented.wrappedValue = false
                            onDismiss?()
                        },
                        confirmLabel: confirmLabel,
                        dismissLabel: dismissLabel,
                        isDestructive: isDestructive
                    )
                    .padding(32)
                }
                .transition(.opacity)
                .animation(.easeOut(duration: 0.2), value: isPresented.wrappedValue)
            }
        }
    }
}

#Preview("Destructive") {
    ConfirmationDialog(
        title: "Delete Club",
        message: "Are you sure you want to delete this club? This action cannot be undone.",
        onConfirm: {},
        onDismiss: {},
        confirmLabel: "Delete",
        isDestructive: true
    )
    .padding()
    .background(KluvsTheme.colors.background)
}

#Preview("Default") {
    ConfirmationDialog(
        title: "Sign Out",
        message: "Are you sure you want to sign out?",
        onConfirm: {},
        onDismiss: {},
        confirmLabel: "Sign Out"
    )
    .padding()
    .background(KluvsTheme.colors.background)
}

#Preview("Single Button") {
    ConfirmationDialog(
        title: "Authentication Error",
        message: "An unexpected error occurred.",
        onConfirm: {},
        onDismiss: {},
        confirmLabel: "OK",
        dismissLabel: nil
    )
    .padding()
    .background(KluvsTheme.colors.background)
}
