import SwiftUI
import DesignSystem

struct NoTabData: View {
    let text: String

    var body: some View {
        VStack {
            Spacer()
            Text(text)
                .kluvsStyle(KluvsTheme.typography.title.medium, feature: true)
                .foregroundColor(KluvsTheme.colors.contentMuted)
                .multilineTextAlignment(.center)
                .padding()
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct NoSectionData: View {
    let text: String

    var body: some View {
        Text(text)
            .kluvsStyle(KluvsTheme.typography.caption, feature: true)
            .foregroundColor(KluvsTheme.colors.contentMuted)
            .padding(.vertical, 8)
    }
}

#Preview("NoTabData") {
    NoTabData(text: "No data available")
}

#Preview("NoSectionData") {
    NoSectionData(text: "No section data")
}
