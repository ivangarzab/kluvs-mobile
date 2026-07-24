import SwiftUI

/// Determinate reading-progress bar (design-system "Reading Progress", see
/// design-system/docs/states.md §4) — a pill-shaped track with a pill-shaped copper fill.
/// Hand-drawn as its own primitive (two nested capsules) rather than styling SwiftUI's native
/// `ProgressView`, mirroring Android's `ProgressBar` — there, this was a genuine bug fix (M3's
/// `LinearProgressIndicator` adds a visible gap + stop-indicator dot by default); on iOS, plain
/// `ProgressView` has no such bug, but building the same standalone primitive keeps the
/// primitive-vs-composite structure identical across platforms (`OwnProgressRow` calls this, the
/// same relationship as on Android) rather than diverging for no functional reason.
///
/// - Parameter percent: 0-100.
public struct ProgressBar: View {
    let percent: Int

    public init(percent: Int) {
        self.percent = percent
    }

    private var fraction: CGFloat { CGFloat(min(max(percent, 0), 100)) / 100 }

    public var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Capsule().fill(KluvsTheme.colors.cardAlt)
                Capsule()
                    .fill(KluvsTheme.colors.accent)
                    .frame(width: geometry.size.width * fraction)
            }
        }
        .frame(height: 4)
    }
}

#Preview {
    VStack(spacing: 16) {
        ProgressBar(percent: 0)
        ProgressBar(percent: 47)
        ProgressBar(percent: 100)
    }
    .padding()
}
