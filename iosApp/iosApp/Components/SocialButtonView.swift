//
//  SocialButtonView.swift
//  iosApp
//
//  OAuth provider button component
//
import SwiftUI

struct SocialButtonView: View {
    let text: String
    let iconName: CustomIcon
    let iconSize: CGFloat
    let backgroundColor: Color
    let textColor: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image.custom(iconName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: iconSize, height: iconSize)
                    .foregroundColor(nil)

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
            iconName: CustomIcon.discord,
            iconSize: 24,
            backgroundColor: .discordBlue,
            textColor: .white,
            action: {}
        )

        SocialButtonView(
            text: "Continue with Google",
            iconName: CustomIcon.discord,
            iconSize: 24,
            backgroundColor: .googleGray,
            textColor: .googleTextGray,
            action: {}
        )
    }
    .padding()
}
