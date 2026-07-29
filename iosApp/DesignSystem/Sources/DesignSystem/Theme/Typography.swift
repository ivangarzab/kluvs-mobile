import SwiftUI

// Two-register type system synced from design-system/tokens.json (typography.font-family / .scale / .tier).
// Serif (EB Garamond): wordmark, display text, page/section headings, book titles (italic).
// Sans (IBM Plex Sans): all UI chrome — body, labels, buttons, tabs, helper text.
// No other font families.
extension Font {
    private enum FontName {
        static let garamondRegular = "EBGaramond-Regular"
        static let garamondMedium = "EBGaramond-Medium"
        static let garamondBold = "EBGaramond-Bold"
        static let garamondItalic = "EBGaramond-Italic"
        static let garamondMediumItalic = "EBGaramond-MediumItalic"
        static let plexSansRegular = "IBMPlexSans-Regular"
        static let plexSansMedium = "IBMPlexSans-Medium"
        static let plexSansBold = "IBMPlexSans-Bold"
    }

    // MARK: - Serif register (EB Garamond)
    public static func ebGaramond(size: CGFloat) -> Font { .custom(FontName.garamondRegular, size: size) }
    public static func ebGaramondMedium(size: CGFloat) -> Font { .custom(FontName.garamondMedium, size: size) }
    public static func ebGaramondBold(size: CGFloat) -> Font { .custom(FontName.garamondBold, size: size) }
    /// Reserved for book titles only.
    public static func ebGaramondItalic(size: CGFloat) -> Font { .custom(FontName.garamondItalic, size: size) }
    public static func ebGaramondMediumItalic(size: CGFloat) -> Font { .custom(FontName.garamondMediumItalic, size: size) }

    // MARK: - Sans register (IBM Plex Sans)
    public static func plexSans(size: CGFloat) -> Font { .custom(FontName.plexSansRegular, size: size) }
    public static func plexSansMedium(size: CGFloat) -> Font { .custom(FontName.plexSansMedium, size: size) }
    public static func plexSansBold(size: CGFloat) -> Font { .custom(FontName.plexSansBold, size: size) }

    // MARK: - Scale (design-system/tokens.json typography.scale)
    public static let kluvsDisplay1 = Font.ebGaramondBold(size: 96)
    public static let kluvsDisplay2 = Font.ebGaramondBold(size: 48)
    public static let kluvsPageHeading = Font.ebGaramondBold(size: 30)
    public static let kluvsSectionHeading = Font.ebGaramondBold(size: 20) // tier.1-section-header
    public static let kluvsCardHeading = Font.ebGaramondBold(size: 18)
    public static let kluvsBodyLg = Font.plexSans(size: 16) // tier.2-primary-content
    public static let kluvsBody = Font.plexSans(size: 14) // tier.3-supporting-details
    public static let kluvsHelper = Font.plexSansMedium(size: 13)
    /// tier.4-fine-print — version numbers, disclaimers.
    public static let kluvsHelperSm = Font.plexSans(size: 12)
    /// component.button.primary
    public static let kluvsButtonPrimary = Font.plexSansMedium(size: 15)
    /// component.button.outlined / .text
    public static let kluvsButtonSecondary = Font.plexSansMedium(size: 14)
    /// Eyebrow pattern — uppercase, tracking 0.1em, applied via `.textCase(.uppercase).kerning(...)` at call sites.
    public static let kluvsEyebrow = Font.plexSansMedium(size: 12)
    /// modal-label — 11px / tracking 0.14em / uppercase, applied at call sites.
    public static let kluvsModalLabel = Font.plexSansMedium(size: 11)
}
