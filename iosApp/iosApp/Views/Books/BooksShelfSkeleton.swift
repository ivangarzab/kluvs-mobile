import SwiftUI
import DesignSystem

/// Placeholder shown while the shelf loads — mirrors ShelfSectionView's shape (eyebrow label,
/// horizontal row of BookCard-shaped covers) so nothing jumps around once real sections swap in.
/// Mirrors Android's `BooksShelfSkeleton`.
struct BooksShelfSkeleton: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            ForEach(0..<2, id: \.self) { _ in
                VStack(alignment: .leading, spacing: 8) {
                    Bone()
                        .frame(width: 80, height: 12)
                        .padding(.horizontal, 16)

                    HStack(spacing: 12) {
                        ForEach(0..<3, id: \.self) { _ in
                            VStack(alignment: .leading, spacing: 4) {
                                Bone()
                                    .aspectRatio(2.0 / 3.0, contentMode: .fit)
                                Bone().frame(width: 96, height: 14)
                                Bone().frame(width: 60, height: 12)
                            }
                            .frame(width: 120)
                        }
                    }
                    .padding(.horizontal, 16)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .padding(.vertical, 16)
    }
}

#Preview {
    BooksShelfSkeleton()
        .background(KluvsTheme.colors.background)
}
