import SwiftUI
import DesignSystem

struct LoadingView: View {
    var body: some View {
        VStack {
            Spacer()
            HStack {
                Spacer()
                LoadingSpinner(size: 48)
                Spacer()
            }
            Spacer()
        }
    }
}

#Preview {
    LoadingView()
}
