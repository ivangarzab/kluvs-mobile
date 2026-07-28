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
        DispatchQueue.main.async {
            context.coordinator.attach(via: anchor)
        }
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
    /// Walks superviews (not the responder chain — a `UIViewRepresentable`-hosted view's
    /// superview chain is what actually leads to the SwiftUI-owned `UIScrollView`).
    func findEnclosingScrollView() -> UIScrollView? {
        var view: UIView? = self
        while let current = view {
            if let scrollView = current as? UIScrollView { return scrollView }
            view = current.superview
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
                .frame(width: 0, height: 0)
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
