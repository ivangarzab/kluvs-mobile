//
//  SocialButtonView.swift
//  iosApp
//
//  OAuth provider button component
//
import SwiftUI

struct SocialButtonView: View {
    let text: String
    let iconName: String // SF Symbol name for now (custom assets can be added later)
    let iconSize: CGFloat
    let backgroundColor: Color
    let textColor: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: iconName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: iconSize, height: iconSize)
                    .foregroundColor(textColor)

                Text(text)
                    .font(.body)
                    .fontWeight(.medium)
                    .foregroundColor(textColor)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 48)
            .background(backgroundColor)
            .cornerRadius(8)
        }
    }
}

#Preview {
    VStack(spacing: 16) {
        SocialButtonView(
            text: "Continue with Discord",
            iconName: "bubble.left.and.bubble.right.fill", // Placeholder SF Symbol
            iconSize: 20,
            backgroundColor: .discordBlue,
            textColor: .white,
            action: {}
        )

        SocialButtonView(
            text: "Continue with Google",
            iconName: "globe", // Placeholder SF Symbol
            iconSize: 24,
            backgroundColor: .googleGray,
            textColor: .googleTextGray,
            action: {}
        )
    }
    .padding()
}
