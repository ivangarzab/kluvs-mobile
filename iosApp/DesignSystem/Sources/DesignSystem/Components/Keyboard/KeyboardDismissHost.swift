import SwiftUI
import UIKit

/// Installs a single app-wide `UITapGestureRecognizer` on the window that resigns first
/// responder on any tap, without consuming the touch — so whatever control is under the
/// finger (button, list row, etc.) still receives it normally.
///
/// SwiftUI has no built-in "tap anywhere to dismiss the keyboard" that reliably reaches into
/// every screen, form, and `kluvsBottomSheet` overlay uniformly — text fields across the app
/// were left with no way to dismiss the keyboard at all. Reaching into UIKit at the window
/// level covers every presentation uniformly in one place, rather than wiring a dismiss gesture
/// into each individual screen (mirrors `PullToRefreshHost`'s approach of reaching into UIKit
/// for a cross-cutting concern).
struct KeyboardDismissHost: UIViewRepresentable {
    func makeUIView(context: Context) -> UIView {
        let anchor = UIView(frame: .zero)
        anchor.backgroundColor = .clear
        anchor.isUserInteractionEnabled = false
        context.coordinator.scheduleAttach(via: anchor)
        return anchor
    }

    func updateUIView(_ uiView: UIView, context: Context) {}

    func makeCoordinator() -> Coordinator { Coordinator() }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        private weak var attachedWindow: UIWindow?

        /// The anchor may not be attached to its window yet on the first runloop tick after
        /// `makeUIView`, so retry a few times with a short delay rather than giving up after a
        /// single attempt (same reasoning as `PullToRefreshHost.scheduleAttach`).
        func scheduleAttach(via anchor: UIView, attemptsRemaining: Int = 5) {
            DispatchQueue.main.async { [weak self, weak anchor] in
                guard let self, let anchor else { return }
                if self.attachedWindow != nil { return }
                if let window = anchor.window {
                    self.attach(to: window)
                } else if attemptsRemaining > 0 {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self, weak anchor] in
                        guard let self, let anchor else { return }
                        self.scheduleAttach(via: anchor, attemptsRemaining: attemptsRemaining - 1)
                    }
                }
            }
        }

        private func attach(to window: UIWindow) {
            guard attachedWindow == nil else { return }
            attachedWindow = window

            let tap = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
            // Doesn't consume the touch — buttons, list rows, and every other control under the
            // finger still receive it normally; this recognizer only piggybacks on the tap.
            tap.cancelsTouchesInView = false
            tap.delegate = self
            window.addGestureRecognizer(tap)
        }

        @objc private func dismissKeyboard() {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }

        func gestureRecognizer(
            _ gestureRecognizer: UIGestureRecognizer,
            shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
        ) -> Bool {
            true
        }
    }
}

public extension View {
    /// Lets any tap anywhere in this view's hierarchy dismiss the on-screen keyboard, without
    /// blocking taps on the controls underneath. Apply once near the app root — see `ContentView`.
    func kluvsDismissKeyboardOnTap() -> some View {
        background(KeyboardDismissHost())
    }
}
