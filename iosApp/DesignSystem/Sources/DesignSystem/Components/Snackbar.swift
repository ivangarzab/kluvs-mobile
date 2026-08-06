import SwiftUI

/// Which of the two design-system-approved looks a snackbar renders in — see
/// design-system/docs/states.md § Snackbar. Mirrors Android's `SnackbarVariant`.
public enum SnackbarVariant: Hashable {
    case neutral
    case danger
}

/// The content of a single snackbar message — mirrors Android's `KluvsSnackbarVisuals`.
/// `action`/`actionLabel` are optional and only rendered together; a label with no closure (or
/// vice versa) is treated as no action, same as leaving both nil.
public struct SnackbarData: Equatable, Hashable {
    public let message: String
    public var variant: SnackbarVariant = .neutral
    public var actionLabel: String?
    public var action: (() -> Void)?

    public init(
        message: String,
        variant: SnackbarVariant = .neutral,
        actionLabel: String? = nil,
        action: (() -> Void)? = nil
    ) {
        self.message = message
        self.variant = variant
        self.actionLabel = actionLabel
        self.action = action
    }

    // `action` is a closure and isn't Equatable/Hashable — excluded deliberately from both;
    // message/variant/actionLabel are what `.snackbar(_:)` needs to compare (and `.id(_:)` needs
    // to hash) to decide whether an incoming message replaces the one currently showing.
    public static func == (lhs: SnackbarData, rhs: SnackbarData) -> Bool {
        lhs.message == rhs.message && lhs.variant == rhs.variant && lhs.actionLabel == rhs.actionLabel
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(message)
        hasher.combine(variant)
        hasher.combine(actionLabel)
    }
}

/// The design-system-branded snackbar surface — see design-system/docs/states.md § Snackbar.
/// Mirrors Android's `KluvsSnackbar`. Not used directly by screens; apply `.snackbar(_:)` instead,
/// which owns show/auto-dismiss timing the way `SnackbarHostState` does on Android.
public struct Snackbar: View {
    public let data: SnackbarData

    public init(data: SnackbarData) {
        self.data = data
    }

    // Danger uses a fully opaque fill (KluvsTheme.colors.danger) with white text/icon
    // (onAccent — the same "white on saturated brand/status surface" token PrimaryButton uses
    // for copper), not the translucent dangerSubtle wash ErrorBanner uses. A subtle tint is
    // legible sitting on a known, fixed background; a snackbar floats over arbitrary content
    // and needs to read clearly regardless of what's behind it.
    public var body: some View {
        HStack(spacing: 8) {
            if data.variant == .danger {
                Icon(type: .error, contentDescription: nil, tint: KluvsTheme.colors.onAccent)
                    .frame(width: 16, height: 16)
            }

            Text(data.message)
                .kluvsStyle(KluvsTheme.typography.caption)
                .foregroundColor(data.variant == .danger ? KluvsTheme.colors.onAccent : KluvsTheme.colors.content)
                .lineLimit(1)
                .truncationMode(.tail)

            if let actionLabel = data.actionLabel, let action = data.action {
                Spacer(minLength: 8)
                TextButton(text: actionLabel, action: action, emphasized: true)
            }
        }
        .padding(.horizontal, 12)
        .frame(height: 54)
        // Two-layer frame is deliberate, not redundant: `.frame(maxWidth: .infinity)` makes the
        // row greedy so it stretches to fill the available width (matching Android's
        // `fillMaxWidth()`); the second `.frame(maxWidth: 400)` then caps that at the design
        // spec's max width on wide/tablet layouts. A single `.frame(maxWidth: 400)` alone does
        // NOT expand — it only caps growth the view isn't otherwise requesting — which is why an
        // earlier version of this rendered as a small content-hugging pill instead of a bar.
        // `alignment: .leading` on both is required too — `.frame`'s default alignment is
        // `.center`, which centers the HStack's (narrower) children inside the now-wide frame
        // instead of leaving them packed at the leading edge like Android's `fillMaxWidth()` row.
        .frame(maxWidth: .infinity, alignment: .leading)
        .frame(maxWidth: 400, alignment: .leading)
        .background(data.variant == .danger ? KluvsTheme.colors.danger : KluvsTheme.colors.bar)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

/// Shows `data` as a bottom-anchored snackbar, then clears the binding after `duration` — the
/// standalone-View equivalent of Android's `SnackbarHostState` for screens that aren't already
/// wrapped in a Scaffold-like container. A message that arrives while one is already showing
/// restarts the timer rather than queuing (matches design-system/docs/states.md § Snackbar),
/// via `.id(data)` forcing the timed dismissal to re-run for the new content.
private struct SnackbarModifier: ViewModifier {
    @Binding var data: SnackbarData?
    var duration: TimeInterval = 4.0

    func body(content: Content) -> some View {
        content.overlay(alignment: .bottom) {
            if let data {
                Snackbar(data: data)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .id(data)
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + duration) {
                            withAnimation { self.data = nil }
                        }
                    }
            }
        }
        .animation(.easeInOut(duration: 0.25), value: data)
    }
}

extension View {
    /// Shows `data` as a transient bottom snackbar, then clears the binding after `duration`.
    public func snackbar(_ data: Binding<SnackbarData?>, duration: TimeInterval = 4.0) -> some View {
        modifier(SnackbarModifier(data: data, duration: duration))
    }
}

#Preview("Neutral") {
    Snackbar(data: SnackbarData(message: "Club created"))
        .padding(16)
        .background(Color.warmDarkBase)
}

#Preview("Danger") {
    Snackbar(data: SnackbarData(message: "Couldn't save changes. Please try again.", variant: .danger))
        .padding(16)
        .background(Color.warmDarkBase)
}

#Preview("With action") {
    Snackbar(data: SnackbarData(message: "Removed from shelf", actionLabel: "Undo", action: {}))
        .padding(16)
        .background(Color.warmDarkBase)
}
