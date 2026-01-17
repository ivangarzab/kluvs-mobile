//
//  InputFieldView.swift
//  iosApp
//
//  Reusable text field component matching Android's InputField
//
import SwiftUI

struct InputFieldView: View {
    let label: String
    @Binding var text: String
    let icon: CustomIcon
    let supportingText: String
    let supportingTextColor: Color
    let isPassword: Bool
    let keyboardType: UIKeyboardType
    let submitLabel: SubmitLabel
    let onSubmit: () -> Void

    @State private var isPasswordVisible: Bool = false

    init(
        label: String,
        text: Binding<String>,
        icon: CustomIcon,
        supportingText: String,
        supportingTextColor: Color = .gray,
        isPassword: Bool = false,
        keyboardType: UIKeyboardType = .default,
        submitLabel: SubmitLabel = .next,
        onSubmit: @escaping () -> Void = {}
    ) {
        self.label = label
        self._text = text
        self.icon = icon
        self.supportingText = supportingText
        self.supportingTextColor = supportingTextColor
        self.isPassword = isPassword
        self.keyboardType = keyboardType
        self.submitLabel = submitLabel
        self.onSubmit = onSubmit
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            // Text field with icon
            HStack(spacing: 12) {
                Image.custom(icon)
                    .foregroundColor(.gray)
                    .frame(width: 20, height: 20)

                if isPassword && !isPasswordVisible {
                    SecureField(label, text: $text)
                        .keyboardType(keyboardType)
                        .submitLabel(submitLabel)
                        .onSubmit(onSubmit)
                } else {
                    TextField(label, text: $text)
                        .keyboardType(keyboardType)
                        .submitLabel(submitLabel)
                        .onSubmit(onSubmit)
                }

                if isPassword {
                    Button(action: {
                        isPasswordVisible.toggle()
                    }) {
                        Image(systemName: isPasswordVisible ? "eye.slash.fill" : "eye.fill")
                            .foregroundColor(.gray)
                    }
                }
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
            )

            // Supporting text
            Text(supportingText)
                .font(.caption)
                .foregroundColor(supportingTextColor)
                .padding(.horizontal, 4)
        }
    }
}

#Preview {
    VStack {
        InputFieldView(
            label: "Email",
            text: .constant(""),
            icon: .email,
            supportingText: "Enter a valid email address",
            supportingTextColor: .gray
        )

        InputFieldView(
            label: "Password",
            text: .constant(""),
            icon: .password,
            supportingText: "Password must be at least 8 characters",
            supportingTextColor: .red,
            isPassword: true
        )
    }
    .padding()
}
