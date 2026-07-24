import SwiftUI

/// Three-zone modal shell — design-system "Modal" (see design-system/docs/modal.md): a header
/// with an eyebrow label, free-form body content, and an optional footer. Reserved for edit/
/// create forms and multi-field flows — confirm-only actions belong in `ConfirmationDialog`
/// instead. Mirrors Android's `BottomSheet`.
///
/// Unlike `ConfirmationDialog`, this uses SwiftUI's real `.sheet(...)` presentation — fully
/// restylable via `.presentationBackground`/`.presentationCornerRadius` (iOS 16.4+), so there's no
/// need for a fully custom overlay here. Present via the `.kluvsBottomSheet(...)` view modifier
/// below, not by constructing this type directly.
struct BottomSheetContent<Content: View, Footer: View>: View {
    let header: String
    var isDestructiveHeader: Bool = false
    @ViewBuilder var content: () -> Content
    @ViewBuilder var footer: () -> Footer

    private var accentColor: Color { isDestructiveHeader ? KluvsTheme.colors.danger : KluvsTheme.colors.accent }

    var body: some View {
        VStack(spacing: 0) {
            Text(header.uppercased())
                .kluvsStyle(KluvsTheme.typography.eyebrow)
                .foregroundColor(accentColor)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)
                .padding(.bottom, 20)

            Rectangle().fill(KluvsTheme.colors.divider).frame(height: 1)

            content()
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

public extension View {
    /// Presents a `BottomSheet` (header + free-form content + optional footer) when `isPresented`
    /// is true, styled with the design-system's `bar` container color and 16pt top corners.
    func kluvsBottomSheet<Content: View, Footer: View>(
        isPresented: Binding<Bool>,
        header: String,
        isDestructiveHeader: Bool = false,
        @ViewBuilder content: @escaping () -> Content,
        @ViewBuilder footer: @escaping () -> Footer = { EmptyView() }
    ) -> some View {
        sheet(isPresented: isPresented) {
            BottomSheetContent(header: header, isDestructiveHeader: isDestructiveHeader, content: content, footer: footer)
                .presentationDragIndicator(.visible)
                .presentationCornerRadius(16)
                .presentationBackground(KluvsTheme.colors.bar)
        }
    }
}

#Preview {
    BottomSheetContent(
        header: "Edit Club",
        content: {
            Text("{form fields would go here}")
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
        },
        footer: {
            BottomSheetFooter(actionLabel: "Save", onAction: {}, onCancel: {})
        }
    )
    .background(KluvsTheme.colors.bar)
}
