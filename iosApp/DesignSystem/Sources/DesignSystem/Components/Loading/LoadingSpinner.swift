import SwiftUI

/// The Kluvs brand loading indicator — Breathe·Tidal animated mark (design-system "Kluvs
/// Loading Spinner", see design-system/docs/spinner-kluvs.md). Ported from the design system's
/// own `assets/ios/KluvsSpinner.swift` reference sample, renamed to `LoadingSpinner` — matches
/// Android's own naming (Android's component was itself renamed away from `KluvsSpinner` for the
/// same no-brand-prefix convention `Icon`/the button family already follow).
///
/// Requires iOS 17+ (`KeyframeAnimator`). Respects `accessibilityReduceMotion` — renders a static
/// rest pose instead of animating when the user has that setting on.
public struct LoadingSpinner: View {
    public var size: CGFloat

    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    public init(size: CGFloat = 32) {
        self.size = size
    }

    public var body: some View {
        Group {
            if reduceMotion {
                // Static rest pose — no motion, respects user preference.
                Image("spinner-kluvs", bundle: .module)
                    .resizable()
                    .scaledToFit()
                    .scaleEffect(0.96)
                    .opacity(0.94)
            } else {
                KeyframeAnimator(
                    initialValue: SpinnerState(),
                    repeating: true
                ) { state in
                    Image("spinner-kluvs", bundle: .module)
                        .resizable()
                        .scaledToFit()
                        .rotationEffect(.degrees(state.rotation))
                        .scaleEffect(state.scale)
                        .opacity(state.opacity)
                } keyframes: { _ in
                    // Rotation: 0° → 120° (inhale) → hold → 240° (exhale) → hold.
                    // 240° per cycle - the mark has 3-fold symmetry, so 240° ≡ 0° visually,
                    // making the loop seam invisible.
                    KeyframeTrack(\.rotation) {
                        MoveKeyframe(0)
                        CubicKeyframe(120, duration: 1.4)   // inhale
                        LinearKeyframe(120, duration: 0.6)  // hold
                        CubicKeyframe(240, duration: 1.4)   // exhale
                        LinearKeyframe(240, duration: 0.6)  // hold
                    }
                    KeyframeTrack(\.scale) {
                        MoveKeyframe(0.96)
                        CubicKeyframe(1.08, duration: 1.4)
                        LinearKeyframe(1.08, duration: 0.6)
                        CubicKeyframe(0.96, duration: 1.4)
                        LinearKeyframe(0.96, duration: 0.6)
                    }
                    KeyframeTrack(\.opacity) {
                        MoveKeyframe(0.94)
                        CubicKeyframe(1.0, duration: 1.4)
                        LinearKeyframe(1.0, duration: 0.6)
                        CubicKeyframe(0.94, duration: 1.4)
                        LinearKeyframe(0.94, duration: 0.6)
                    }
                }
            }
        }
        .frame(width: size, height: size)
    }
}

// NOTE: CubicKeyframe uses SwiftUI's natural cubic spline interpolation, a close approximation
// of the spec's cubic-bezier(0.4, 0, 0.4, 1) - the perceptual difference is negligible for this
// slow breathing motion (same caveat the design-system reference sample itself carries).

private struct SpinnerState {
    var rotation: Double = 0
    var scale: Double = 0.96
    var opacity: Double = 0.94
}

#Preview {
    VStack(spacing: 32) {
        LoadingSpinner(size: 16)
        LoadingSpinner(size: 32)
        LoadingSpinner(size: 64)
    }
    .padding(40)
    .background(KluvsTheme.colors.background)
}
