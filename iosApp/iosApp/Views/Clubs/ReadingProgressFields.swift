import SwiftUI
import Shared
import DesignSystem

/// Reading Progress sheet body — presented via `.kluvsBottomSheet` at the call site, not as its
/// own `View` wrapping a `.sheet`. Checked kluvs-frontend's ReadingProgressModal.tsx as source of
/// truth: already matched closely functionally (Page/Percent toggle, auto "mark as finished" at
/// the end of the book, manual finished toggle) — this pass swaps the hand-rolled Track-By pill
/// row for `ToggleControl` (matching web's segmented control and mirroring the same component used
/// in Share Club) and adds the elevated "BOOK" info box web shows, which this sheet previously
/// rendered as a plain secondary-colored Text line instead.
struct ReadingProgressFields: View {
    let bookTitle: String
    let pageCount: Int32?
    @Binding var progressType: Shared.ProgressType
    @Binding var currentPageText: String
    @Binding var percentText: String
    @Binding var markFinished: Bool
    @Binding var lastAutoTriggerValue: String?

    private var previewPercent: Int? {
        guard progressType == .page, let pageCount, pageCount > 0,
              let page = Int32(currentPageText) else { return nil }
        return Swift.min(100, Int((Float(page) * 100 / Float(pageCount)).rounded()))
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 4) {
                Text("BOOK")
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                Text(bookTitle)
                    .kluvsStyle(KluvsTheme.typography.body.medium)
                    .foregroundColor(KluvsTheme.colors.content)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(KluvsTheme.colors.cardAlt)
            .overlay(RoundedRectangle(cornerRadius: 8).strokeBorder(KluvsTheme.colors.divider, lineWidth: 1))
            .clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading, spacing: 8) {
                Text("TRACK BY")
                    .kluvsStyle(KluvsTheme.typography.eyebrow)
                    .foregroundColor(KluvsTheme.colors.contentMuted)
                ToggleControl(
                    options: [Shared.ProgressType.page, Shared.ProgressType.percent],
                    selected: progressType,
                    onSelect: { progressType = $0 },
                    label: { $0 == Shared.ProgressType.page ? "Page" : "Percent" }
                )
            }

            if progressType == .page {
                VStack(alignment: .leading, spacing: 4) {
                    InputField(
                        label: pageCount != nil ? "Current Page (of \(pageCount!))" : "Current Page",
                        value: $currentPageText,
                        keyboardType: .numberPad
                    )
                    .onChange(of: currentPageText) { _, newValue in
                        currentPageText = newValue.filter { $0.isNumber }
                        autoToggleFinished(currentPageText)
                    }
                    if let previewPercent {
                        Text("That's about \(previewPercent)% complete.")
                            .kluvsStyle(KluvsTheme.typography.caption)
                            .foregroundColor(KluvsTheme.colors.contentMuted)
                    }
                }
            } else {
                InputField(label: "Percent Complete", value: $percentText, keyboardType: .decimalPad)
                    .onChange(of: percentText) { _, newValue in
                        percentText = newValue.filter { $0.isNumber || $0 == "." }
                        autoToggleFinished(percentText)
                    }
            }

            Toggle("Mark as finished", isOn: $markFinished)
                .kluvsStyle(KluvsTheme.typography.body.medium)
                .foregroundColor(KluvsTheme.colors.content)
                .tint(KluvsTheme.colors.accent)
        }
    }

    private func autoToggleFinished(_ newValue: String) {
        guard newValue != lastAutoTriggerValue else { return }
        let atEnd: Bool
        if progressType == .page, let pageCount, pageCount > 0 {
            atEnd = (Int32(newValue) ?? 0) >= pageCount
        } else if progressType == .percent {
            atEnd = (Float(newValue) ?? 0) >= 100
        } else {
            return
        }
        if atEnd != markFinished {
            markFinished = atEnd
            lastAutoTriggerValue = newValue
        }
    }
}
