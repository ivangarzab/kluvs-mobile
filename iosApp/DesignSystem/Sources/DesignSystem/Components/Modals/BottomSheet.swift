import SwiftUI
import UIKit

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

/// Publishes the on-screen keyboard's current height so a non-`.sheet` presentation (which gets
/// none of the automatic keyboard avoidance a real `.sheet`/`Form` gets from its own hosting
/// controller) can shift itself up manually. `keyboardWillChangeFrame`'s frame already accounts
/// for the safe area the keyboard covers, so subtracting it from screen height gives the exact
/// visible height, animated to match the keyboard's own show/hide timing.
private final class KeyboardHeightObserver: ObservableObject {
    @Published private(set) var height: CGFloat = 0

    private var showObserver: NSObjectProtocol?
    private var hideObserver: NSObjectProtocol?

    init() {
        showObserver = NotificationCenter.default.addObserver(
            forName: UIResponder.keyboardWillChangeFrameNotification,
            object: nil,
            queue: .main
        ) { [weak self] notification in
            guard let frame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else { return }
            let newHeight = max(0, UIScreen.main.bounds.height - frame.origin.y)
            withAnimation(.easeOut(duration: 0.25)) { self?.height = newHeight }
        }
        hideObserver = NotificationCenter.default.addObserver(
            forName: UIResponder.keyboardWillHideNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            withAnimation(.easeOut(duration: 0.25)) { self?.height = 0 }
        }
    }

    deinit {
        if let showObserver { NotificationCenter.default.removeObserver(showObserver) }
        if let hideObserver { NotificationCenter.default.removeObserver(hideObserver) }
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
    var onDismiss: (() -> Void)?
    @ViewBuilder let content: () -> Content
    @ViewBuilder let footer: () -> Footer

    @GestureState private var dragTranslation: CGFloat = 0
    // Being a fully custom overlay (not a real `.sheet`), this sheet gets none of the automatic
    // keyboard-avoidance a `.sheet`/`Form` gets for free — an input field near the bottom of the
    // sheet (e.g. Reading Progress's page/percent field) would otherwise sit right behind the
    // keyboard with no way to see what's being typed.
    @StateObject private var keyboard = KeyboardHeightObserver()

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
                    .offset(y: max(0, dragTranslation) - keyboard.height)
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
        .onChange(of: isPresented) { _, newValue in
            // Fires on any dismissal path (scrim tap, drag, or a caller setting isPresented
            // false directly, e.g. after a footer Save action) — matches native .sheet's own
            // onDismiss semantics, which fire regardless of *how* the sheet closed.
            if !newValue { onDismiss?() }
        }
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
        onDismiss: (() -> Void)? = nil,
        @ViewBuilder content: @escaping () -> Content,
        @ViewBuilder footer: @escaping () -> Footer = { EmptyView() }
    ) -> some View {
        overlay {
            DraggableBottomSheet(isPresented: isPresented, header: header, isDestructiveHeader: isDestructiveHeader, onDismiss: onDismiss, content: content, footer: footer)
        }
    }

    /// `item`-driven variant, for call sites presenting per-item state (e.g. "editing discussion
    /// X") instead of a plain `Bool`. Retains the last non-nil item across the dismiss animation
    /// (the same way SwiftUI's own `.sheet(item:)` behaves) so `content`/`footer` never see `nil`
    /// mid-fade-out — `item` is typically cleared to `nil` right as the dismiss begins, and without
    /// this the closures would have nothing to render for that last animated frame.
    func kluvsBottomSheet<Item: Identifiable, Content: View, Footer: View>(
        item: Binding<Item?>,
        header: String,
        isDestructiveHeader: Bool = false,
        @ViewBuilder content: @escaping (Item) -> Content,
        @ViewBuilder footer: @escaping (Item) -> Footer
    ) -> some View {
        overlay {
            ItemBottomSheetHost(item: item, header: header, isDestructiveHeader: isDestructiveHeader, content: content, footer: footer)
        }
    }

    /// No-footer convenience — a default value of `{ _ in EmptyView() }` on the overload above
    /// doesn't compile: default-argument expressions can't reference the function's own generic
    /// parameters (`Item`), so `_` has nothing to infer its type from. A real overload sidesteps
    /// that since `Item` is bound normally in a function body, not a default-expression context.
    func kluvsBottomSheet<Item: Identifiable, Content: View>(
        item: Binding<Item?>,
        header: String,
        isDestructiveHeader: Bool = false,
        @ViewBuilder content: @escaping (Item) -> Content
    ) -> some View {
        kluvsBottomSheet(
            item: item,
            header: header,
            isDestructiveHeader: isDestructiveHeader,
            content: content,
            footer: { (_: Item) in EmptyView() }
        )
    }
}

private struct ItemBottomSheetHost<Item: Identifiable, Content: View, Footer: View>: View {
    @Binding var item: Item?
    let header: String
    var isDestructiveHeader: Bool
    let content: (Item) -> Content
    let footer: (Item) -> Footer

    @State private var cachedItem: Item?

    var body: some View {
        DraggableBottomSheet(
            isPresented: Binding(get: { item != nil }, set: { if !$0 { item = nil } }),
            header: header,
            isDestructiveHeader: isDestructiveHeader,
            content: { if let cachedItem { content(cachedItem) } },
            footer: { if let cachedItem { footer(cachedItem) } }
        )
        .onChange(of: item?.id) { _, _ in
            if let item { cachedItem = item }
        }
        .onAppear { cachedItem = item }
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
