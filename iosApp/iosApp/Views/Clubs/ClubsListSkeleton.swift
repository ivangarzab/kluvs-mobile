import SwiftUI
import DesignSystem

/// Placeholder rows shown while the club list loads — mirrors ClubListRow's shape (cover
/// thumb, name + subtitle lines, avatar dots) so nothing jumps around once real rows swap in.
/// Mirrors Android's `ClubsListSkeleton`.
struct ClubsListSkeleton: View {
    var body: some View {
        VStack(spacing: 0) {
            ForEach(0..<5, id: \.self) { _ in
                VStack(spacing: 0) {
                    HStack(spacing: 12) {
                        Bone()
                            .frame(width: 40)
                            .aspectRatio(2.0 / 3.0, contentMode: .fit)

                        VStack(alignment: .leading, spacing: 8) {
                            Bone().frame(width: 140, height: 16)
                            Bone().frame(width: 90, height: 12)
                            HStack(spacing: 4) {
                                ForEach(0..<3, id: \.self) { _ in
                                    Bone(Circle()).frame(width: 20, height: 20)
                                }
                            }
                        }
                        Spacer()
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 16)

                    Divider()
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

#Preview {
    ClubsListSkeleton()
        .background(KluvsTheme.colors.background)
}
