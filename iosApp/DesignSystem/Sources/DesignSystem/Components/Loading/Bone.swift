import SwiftUI

/// One shimmering skeleton "bone" — a pulsing placeholder shape shown while content loads.
/// Mirrors Android's `rememberShimmerColor()` pulse (0.3 -> 0.7 alpha on `contentMuted`, 0.8s
/// ease-in-out, reversing, forever), but each instance runs its own identical animation curve
/// instead of sharing one `Color` value across a whole skeleton — SwiftUI has no equivalent to
/// a single `@Composable` call memoizing one animated value for reuse by sibling views. In
/// practice every bone's `.onAppear` fires within the same runloop frame as its siblings, so
/// they still read as synchronized without the extra state plumbing.
public struct Bone: View {
    private let shape: AnyShape
    @State private var animate = false

    public init<S: Shape>(_ shape: S = RoundedRectangle(cornerRadius: 4)) {
        self.shape = AnyShape(shape)
    }

    public var body: some View {
        shape
            .fill(KluvsTheme.colors.contentMuted.opacity(animate ? 0.7 : 0.3))
            .onAppear {
                withAnimation(.easeInOut(duration: 0.8).repeatForever(autoreverses: true)) {
                    animate = true
                }
            }
    }
}

#Preview {
    VStack(alignment: .leading, spacing: 12) {
        Bone().frame(width: 80, height: 12)
        Bone().frame(width: 160, height: 16)
        HStack(spacing: 8) {
            Bone(Circle()).frame(width: 40, height: 40)
            Bone(Circle()).frame(width: 40, height: 40)
        }
    }
    .padding()
    .background(KluvsTheme.colors.background)
}
