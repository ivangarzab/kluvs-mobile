//
//  TextDividerView.swift
//  iosApp
//
//  "OR" divider with horizontal lines
//
import SwiftUI

struct TextDividerView: View {
    let text: String

    var body: some View {
        HStack(spacing: 16) {
            Rectangle()
                .fill(Color.gray.opacity(0.3))
                .frame(height: 1)

            Text(text)
                .font(.caption)
                .foregroundColor(.gray)

            Rectangle()
                .fill(Color.gray.opacity(0.3))
                .frame(height: 1)
        }
    }
}

#Preview {
    TextDividerView(text: "OR")
        .padding()
}
