import SwiftUI
import DesignSystem

/// Placeholder shown while a club's details load — mirrors ClubDetailView's shape (meta row,
/// tab row, a generic content block) so the tab row doesn't sit there statically before the
/// rest of the screen is ready. Doesn't attempt to mirror each tab's exact, state-dependent
/// layout (session vs. no session, role-gated actions) — just enough shape that nothing jumps
/// around once the real tab row and content swap in. Mirrors Android's `ClubDetailsSkeleton`.
struct ClubDetailsSkeleton: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack(spacing: 8) {
                Bone().frame(width: 50, height: 12)
                Bone().frame(width: 70, height: 12)
                Bone().frame(width: 60, height: 12)
            }

            HStack(spacing: 24) {
                ForEach(0..<3, id: \.self) { _ in
                    Bone().frame(width: 70, height: 14)
                }
            }

            Divider()

            VStack(alignment: .leading, spacing: 12) {
                Bone().frame(width: 160, height: 20)
                Bone(RoundedRectangle(cornerRadius: 8))
                    .frame(maxWidth: .infinity)
                    .frame(height: 80)
                HStack(spacing: 8) {
                    ForEach(0..<4, id: \.self) { _ in
                        Bone(Circle()).frame(width: 28, height: 28)
                    }
                }
            }
        }
        .padding(.horizontal, 16)
    }
}

#Preview {
    ClubDetailsSkeleton()
        .background(KluvsTheme.colors.background)
        .padding(.vertical, 16)
}
