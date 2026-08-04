import SwiftUI

/// Branded segmented tab selector — an underline indicator under the selected label, not a
/// pill/capsule. SwiftUI has no native equivalent to Android's `TabRow`/`TabRowDefaults
/// .SecondaryIndicator` (the closest native control, `Picker` with `.segmentedPickerStyle()`,
/// renders as a system-grey capsule that doesn't take branding), so this hand-builds the same
/// visual language: `KluvsTheme.typography.label` text, accent-colored underline that animates
/// between tabs, `colors.background` container. Mirrors Android's `TabRow` usage in
/// `ClubsScreen`.
public struct TabRow: View {
    @Binding var selectedIndex: Int
    let titles: [String]

    @Namespace private var indicatorNamespace

    public init(selectedIndex: Binding<Int>, titles: [String]) {
        self._selectedIndex = selectedIndex
        self.titles = titles
    }

    public var body: some View {
        HStack(spacing: 0) {
            ForEach(Array(titles.enumerated()), id: \.offset) { index, title in
                let isSelected = index == selectedIndex
                Button(action: { selectedIndex = index }) {
                    VStack(spacing: 8) {
                        Text(title)
                            .kluvsStyle(KluvsTheme.typography.label)
                            .foregroundColor(isSelected ? KluvsTheme.colors.accent : KluvsTheme.colors.contentMuted)
                            .frame(maxWidth: .infinity)

                        ZStack {
                            Rectangle()
                                .fill(Color.clear)
                                .frame(height: 2)
                            if isSelected {
                                Rectangle()
                                    .fill(KluvsTheme.colors.accent)
                                    .frame(height: 2)
                                    .matchedGeometryEffect(id: "indicator", in: indicatorNamespace)
                            }
                        }
                    }
                }
                .buttonStyle(.plain)
            }
        }
        // M3's own `Tab` enforces a 48dp minimum row height; matching it here (top padding alone
        // left this visibly thinner than Android's tab row).
        .padding(.vertical, 12)
        .background(KluvsTheme.colors.background)
        .animation(.easeInOut(duration: 0.2), value: selectedIndex)
    }
}

#Preview {
    StatefulTabRowPreview()
}

private struct StatefulTabRowPreview: View {
    @State private var selected = 0

    var body: some View {
        TabRow(selectedIndex: $selected, titles: ["General", "Discussions", "Members"])
            .background(KluvsTheme.colors.background)
    }
}
