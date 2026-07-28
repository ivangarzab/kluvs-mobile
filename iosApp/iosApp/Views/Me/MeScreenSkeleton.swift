import SwiftUI
import DesignSystem

/// Full-screen placeholder shown while the profile loads — mirrors MeView's shape (profile
/// row, stats row, up-next card, shelf rows) so nothing jumps around once the real content
/// swaps in. Mirrors Android's `MeScreenSkeleton`.
struct MeScreenSkeleton: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .center) {
                Bone(Circle()).frame(width: 60, height: 60)
                VStack(alignment: .leading, spacing: 8) {
                    Bone().frame(width: 120, height: 20)
                    Bone().frame(width: 80, height: 14)
                }
                .padding(.leading, 8)
            }

            Divider()

            HStack {
                ForEach(0..<3, id: \.self) { _ in
                    VStack(spacing: 6) {
                        Bone().frame(width: 32, height: 20)
                        Bone().frame(width: 48, height: 12)
                    }
                    .frame(maxWidth: .infinity)
                }
            }

            Divider()

            VStack(alignment: .leading, spacing: 8) {
                Bone().frame(width: 80, height: 12)
                Bone().frame(width: 240, height: 24)
            }

            Divider()

            VStack(alignment: .leading, spacing: 12) {
                Bone().frame(width: 100, height: 12)
                ForEach(0..<2, id: \.self) { _ in
                    HStack(spacing: 12) {
                        Bone().frame(width: 64, height: 96)
                        VStack(alignment: .leading, spacing: 8) {
                            Bone().frame(width: 160, height: 16)
                            Bone().frame(width: 100, height: 12)
                        }
                        .padding(.top, 4)
                    }
                }
            }
        }
        .padding(16)
        .background(KluvsTheme.colors.background)
    }
}

#Preview {
    MeScreenSkeleton()
}
