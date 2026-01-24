import SwiftUI

/**
 * Displays a member's avatar image with fallback to placeholder.
 */
struct MemberAvatar: View {
    let avatarUrl: String?
    let size: CGFloat
    var isLoading: Bool = false
    var onClick: (() -> Void)? = nil

    var body: some View {
        ZStack {
            if let urlString = avatarUrl, !urlString.isEmpty, let url = URL(string: urlString) {
                // Load image from URL
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .empty:
                        placeholderView
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(width: size, height: size)
                            .clipShape(Circle())
                    case .failure:
                        placeholderView
                    @unknown default:
                        placeholderView
                    }
                }
            } else {
                // Placeholder
                placeholderView
            }

            // Loading overlay
            if isLoading {
                Circle()
                    .fill(Color.black.opacity(0.5))
                    .frame(width: size, height: size)

                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .scaleEffect(size / 60)
            }
        }
        .frame(width: size, height: size)
        .onTapGesture {
            onClick?()
        }
    }

    private var placeholderView: some View {
        Image("img_fallback")
            .resizable()
            .scaledToFill()
            .frame(width: size, height: size)
            .clipShape(Circle())
    }
}

#Preview {
    VStack(spacing: 20) {
        MemberAvatar(avatarUrl: nil, size: 60)
        MemberAvatar(avatarUrl: nil, size: 60, isLoading: true)
        MemberAvatar(avatarUrl: "https://example.com/avatar.jpg", size: 60)
    }
}
