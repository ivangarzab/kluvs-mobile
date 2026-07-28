import SwiftUI
import UIKit

/// Branded pull-to-refresh, mirroring Android's `PullToRefreshContainer`: the system spinner is
/// hidden and a circular `LoadingSpinner` mark scales/fades in with pull distance instead.
///
/// SwiftUI's `.refreshable` has no equivalent to Android's `PullToRefreshState.distanceFraction`
/// and cannot have its system indicator hidden or replaced — so this reaches into UIKit and
/// installs a `UIRefreshControl` (tinted clear) directly onto the enclosing `UIScrollView`,
/// found by walking up from an invisible anchor view. `contentOffset` KVO drives the pull
/// progress fed back to the SwiftUI overlay.
struct PullToRefreshHost: UIViewRepresentable {
    var isRefreshing: Bool
    var onRefresh: () -> Void
    var onProgressChange: (CGFloat) -> Void

    /// Pull distance (points) at which the branded mark reaches full scale/opacity —
    /// matches the rough travel of Android's own indicator before release-to-refresh.
    private static let pullThreshold: CGFloat = 60

    func makeUIView(context: Context) -> UIView {
        let anchor = UIView(frame: .zero)
        anchor.backgroundColor = .clear
        anchor.isUserInteractionEnabled = false
        context.coordinator.scheduleAttach(via: anchor)
        return anchor
    }

    func updateUIView(_ uiView: UIView, context: Context) {
        context.coordinator.onRefresh = onRefresh
        context.coordinator.setRefreshing(isRefreshing)
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(onProgressChange: onProgressChange)
    }

    final class Coordinator: NSObject {
        var onRefresh: () -> Void = {}
        let onProgressChange: (CGFloat) -> Void
        private weak var scrollView: UIScrollView?
        private var refreshControl: UIRefreshControl?
        private var offsetObservation: NSKeyValueObservation?

        init(onProgressChange: @escaping (CGFloat) -> Void) {
            self.onProgressChange = onProgressChange
        }

        /// The anchor may not be attached to its final hierarchy/window yet on the first
        /// runloop tick after `makeUIView`, so retry a few times with a short delay rather
        /// than giving up after a single attempt.
        func scheduleAttach(via anchor: UIView, attemptsRemaining: Int = 5) {
            DispatchQueue.main.async { [weak self, weak anchor] in
                guard let self, let anchor else { return }
                if self.scrollView != nil { return }
                if anchor.findEnclosingScrollView() != nil {
                    self.attach(via: anchor)
                } else if attemptsRemaining > 0 {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self, weak anchor] in
                        guard let self, let anchor else { return }
                        self.scheduleAttach(via: anchor, attemptsRemaining: attemptsRemaining - 1)
                    }
                }
            }
        }

        func attach(via anchor: UIView) {
            guard scrollView == nil, let scrollView = anchor.findEnclosingScrollView() else { return }
            self.scrollView = scrollView

            let control = UIRefreshControl()
            control.tintColor = .clear
            control.addTarget(self, action: #selector(refreshTriggered), for: .valueChanged)
            scrollView.refreshControl = control
            self.refreshControl = control

            offsetObservation = scrollView.observe(\.contentOffset, options: [.new]) { [weak self] scrollView, _ in
                guard let self else { return }
                let pullDistance = -(scrollView.contentInset.top + scrollView.contentOffset.y)
                let progress = (self.refreshControl?.isRefreshing ?? false)
                    ? 1.0
                    : min(max(pullDistance / PullToRefreshHost.pullThreshold, 0), 1)
                self.onProgressChange(progress)
            }
        }

        func setRefreshing(_ refreshing: Bool) {
            guard let control = refreshControl else { return }
            if refreshing, !control.isRefreshing {
                control.beginRefreshing()
            } else if !refreshing, control.isRefreshing {
                control.endRefreshing()
                onProgressChange(0)
            }
        }

        @objc private func refreshTriggered() {
            onRefresh()
        }
    }
}

private extension UIView {
    /// Finds the nearest `UIScrollView` sharing this view's hierarchy. Deliberately doesn't just
    /// walk superviews: attaching this anchor via `.background()`/`.overlay()` places it as a
    /// *sibling* of the SwiftUI `ScrollView`'s backing `UIScrollView` inside a shared container,
    /// not as its descendant — a pure upward walk never finds it. Instead, walk upward a few
    /// levels (past that shared container) and, at each level, search back down through every
    /// subview for a `UIScrollView`, which works regardless of which exact ancestor turns out to
    /// be the common one.
    func findEnclosingScrollView() -> UIScrollView? {
        var ancestor: UIView? = self
        for _ in 0..<8 {
            guard let current = ancestor else { break }
            if let found = current.firstScrollViewInSubtree() { return found }
            ancestor = current.superview
        }
        return nil
    }

    private func firstScrollViewInSubtree() -> UIScrollView? {
        if let scrollView = self as? UIScrollView { return scrollView }
        for subview in subviews {
            if let found = subview.firstScrollViewInSubtree() { return found }
        }
        return nil
    }
}

private struct PullToRefreshModifier: ViewModifier {
    let isRefreshing: Bool
    let onRefresh: () -> Void
    @State private var progress: CGFloat = 0

    func body(content: Content) -> some View {
        content
            .background(
                PullToRefreshHost(
                    isRefreshing: isRefreshing,
                    onRefresh: onRefresh,
                    onProgressChange: { progress = $0 }
                )
                .frame(width: 1, height: 1)
            )
            .overlay(alignment: .top) {
                if progress > 0 {
                    Circle()
                        .fill(KluvsTheme.colors.card)
                        .frame(width: 48, height: 48)
                        .shadow(radius: 4)
                        .overlay(LoadingSpinner(size: 28))
                        .scaleEffect(progress)
                        .opacity(progress)
                        .padding(.top, 16)
                        .allowsHitTesting(false)
                }
            }
    }
}

public extension View {
    /// Branded pull-to-refresh — see `PullToRefreshHost` for why this isn't just `.refreshable`.
    func kluvsPullToRefresh(isRefreshing: Bool, onRefresh: @escaping () -> Void) -> some View {
        modifier(PullToRefreshModifier(isRefreshing: isRefreshing, onRefresh: onRefresh))
    }
}
