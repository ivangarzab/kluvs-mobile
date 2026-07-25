import SwiftUI

/// Three-zone modal shell — design-system "Modal" (see design-system/docs/modal.md): a header
/// with an eyebrow label, free-form body content, and an optional footer. Reserved for edit/
/// create forms and multi-field flows — confirm-only actions belong in `ConfirmationDialog`
/// instead. Mirrors Android's `BottomSheet`.
///
/// Built as a fully custom overlay, not SwiftUI's real `.sheet(...)` — same reasoning as
/// `ConfirmationDialog`. `.sheet`'s dynamic-height detents (`.presentationDetents([.height(...)])`)
/// turned out to be unreliable once the measured height changes *after* the sheet is already
/// presented: the presentation controller doesn't always resize its container to match, so content
/// renders squashed into a stale, near-zero-height viewport. `.medium`/`.large` avoid that bug but
/// are fixed fractions of the *device* screen, not the content — wrong for a shell used by sheets
/// of very different lengths (a single field vs. a multi-field form). A plain overlay + VStack
/// just hugs whatever content it's given, no measurement dance required. Present via the
/// `.kluvsBottomSheet(...)` view modifier below, not by constructing this type directly.
struct BottomSheetContent<Content: View, Footer: View>: View {
    let header: String
    var isDestructiveHeader: Bool = false
    @ViewBuilder var content: () -> Content
    @ViewBuilder var footer: () -> Footer

    private var accentColor: Color { isDestructiveHeader ? KluvsTheme.colors.danger : KluvsTheme.colors.accent }

    var body: some View {
        VStack(spacing: 0) {
            Capsule()
                .fill(KluvsTheme.colors.divider)
                .frame(width: 36, height: 5)
                .padding(.top, 8)
                .padding(.bottom, 12)

            Text(header.uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(accentColor)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)
                .padding(.bottom, 20)

            Rectangle().fill(KluvsTheme.colors.divider).frame(height: 1)

            content()
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(24)

            if Footer.self != EmptyView.self {
                Rectangle().fill(KluvsTheme.colors.divider).frame(height: 1)
                footer()
            }
        }
    }
}

/// Standard Cancel/Action row for `BottomSheet`'s footer — Cancel always leading, the primary
/// action always trailing, per design-system/docs/modal.md's Footer spec. Mirrors Android's
/// `BottomSheetFooter`.
public struct BottomSheetFooter: View {
    let actionLabel: String
    let onAction: () -> Void
    let onCancel: () -> Void
    var cancelLabel: String
    var actionEnabled: Bool

    public init(
        actionLabel: String,
        onAction: @escaping () -> Void,
        onCancel: @escaping () -> Void,
        cancelLabel: String = "Cancel",
        actionEnabled: Bool = true
    ) {
        self.actionLabel = actionLabel
        self.onAction = onAction
        self.onCancel = onCancel
        self.cancelLabel = cancelLabel
        self.actionEnabled = actionEnabled
    }

    public var body: some View {
        HStack {
            TextButton(text: cancelLabel, action: onCancel)
            Spacer()
            PrimaryButton(text: actionLabel, action: onAction, enabled: actionEnabled)
        }
        .padding(.horizontal, 24)
        .padding(.vertical, 16)
    }
}

/// Owns the slide-in/out animation and drag-to-dismiss gesture for `kluvsBottomSheet`. Has to be a
/// real `View` (not inline in the modifier below) so its `@GestureState` drag offset and the
/// `.animation(value:)` that drives the show/hide transition both live on a stable ancestor that
/// exists whether or not the sheet is currently presented — attaching either directly inside the
/// `if isPresented` conditional silently no-ops, since that view only exists on one side of the
/// toggle and SwiftUI has no "before" state to animate from/to.
private struct DraggableBottomSheet<Content: View, Footer: View>: View {
    @Binding var isPresented: Bool
    let header: String
    var isDestructiveHeader: Bool
    @ViewBuilder let content: () -> Content
    @ViewBuilder let footer: () -> Footer

    @GestureState private var dragTranslation: CGFloat = 0

    var body: some View {
        ZStack(alignment: .bottom) {
            if isPresented {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture { isPresented = false }
                    .transition(.opacity)

                BottomSheetContent(header: header, isDestructiveHeader: isDestructiveHeader, content: content, footer: footer)
                    .padding(.bottom, 8)
                    .background(
                        KluvsTheme.colors.bar
                            .clipShape(.rect(topLeadingRadius: 16, topTrailingRadius: 16))
                            .ignoresSafeArea(edges: .bottom)
                    )
                    .offset(y: max(0, dragTranslation))
                    .gesture(
                        DragGesture()
                            .updating($dragTranslation) { value, state, _ in
                                state = value.translation.height
                            }
                            .onEnded { value in
                                if value.translation.height > 100 {
                                    isPresented = false
                                }
                            }
                    )
                    .transition(.move(edge: .bottom))
            }
        }
        .animation(.easeOut(duration: 0.25), value: isPresented)
    }
}

public extension View {
    /// Presents a `BottomSheet` (header + free-form content + optional footer) when `isPresented`
    /// is true, styled with the design-system's `bar` container color and 16pt top corners, sized
    /// to hug its actual content — not a fixed fraction of the screen. Dismisses on scrim tap or a
    /// downward drag past ~100pt.
    func kluvsBottomSheet<Content: View, Footer: View>(
        isPresented: Binding<Bool>,
        header: String,
        isDestructiveHeader: Bool = false,
        @ViewBuilder content: @escaping () -> Content,
        @ViewBuilder footer: @escaping () -> Footer = { EmptyView() }
    ) -> some View {
        overlay {
            DraggableBottomSheet(isPresented: isPresented, header: header, isDestructiveHeader: isDestructiveHeader, content: content, footer: footer)
        }
    }
}

#Preview {
    Color.gray.opacity(0.2)
        .ignoresSafeArea()
        .kluvsBottomSheet(isPresented: .constant(true), header: "Edit Club") {
            Text("{form fields would go here}")
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
        } footer: {
            BottomSheetFooter(actionLabel: "Save", onAction: {}, onCancel: {})
        }
}
