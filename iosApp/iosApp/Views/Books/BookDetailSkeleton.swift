import SwiftUI
import DesignSystem

/// Full-body placeholder shown while enrichment loads — mirrors BookDetailView's shape
/// (cover + title block, chip row, action row, then About/Details/Author/More-by sections) so
/// nothing jumps around once the real content swaps in. Deliberately shown for the *entire*
/// body rather than per-section, so the screen waits for enrichment to fully resolve before
/// revealing anything, rather than piecemeal-revealing sections as their data arrives.
/// Mirrors Android's `BookDetailSkeleton`.
struct BookDetailSkeleton: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack(spacing: 16) {
                Bone()
                    .frame(width: 120)
                    .aspectRatio(2.0 / 3.0, contentMode: .fit)

                VStack(alignment: .leading, spacing: 8) {
                    Bone().frame(height: 20).frame(maxWidth: .infinity)
                    Bone().frame(width: 140, height: 14)
                    Bone().frame(width: 100, height: 12)
                }
                .padding(.top, 4)
            }

            HStack(spacing: 8) {
                Bone(Capsule()).frame(width: 64, height: 28)
                Bone(Capsule()).frame(width: 48, height: 28)
            }

            HStack(spacing: 10) {
                Bone(Circle()).frame(width: 40, height: 40)
                Bone(RoundedRectangle(cornerRadius: 20)).frame(width: 120, height: 40)
            }

            Divider()

            VStack(alignment: .leading, spacing: 12) {
                Bone().frame(width: 60, height: 12)
                Bone().frame(height: 14).frame(maxWidth: .infinity)
                Bone().frame(height: 14).frame(maxWidth: .infinity)
                Bone().frame(width: 200, height: 14)
            }

            Divider()

            VStack(alignment: .leading, spacing: 12) {
                Bone().frame(width: 60, height: 12)
                ForEach(0..<3, id: \.self) { _ in
                    Bone().frame(height: 14).frame(maxWidth: .infinity)
                }
            }

            Divider()

            VStack(alignment: .leading, spacing: 12) {
                Bone().frame(width: 60, height: 12)
                HStack(spacing: 12) {
                    Bone(Circle()).frame(width: 48, height: 48)
                    Bone().frame(width: 140, height: 16)
                }
                Bone().frame(height: 12).frame(maxWidth: .infinity)
                Bone().frame(width: 220, height: 12)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

#Preview {
    BookDetailSkeleton()
        .background(KluvsTheme.colors.background)
        .padding(16)
}
