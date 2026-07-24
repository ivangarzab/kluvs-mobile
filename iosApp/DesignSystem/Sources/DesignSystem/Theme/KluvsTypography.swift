import SwiftUI

/// A single Kluvs type style — bundles what Compose's `TextStyle` bundles in one value (font +
/// tracking + line spacing), since SwiftUI has no equivalent single-value type: `Font` alone
/// carries none of those. Apply via the `.kluvsStyle(_:feature:)` view modifier below, not by
/// reading `.font` directly, so tracking/line-spacing/feature are never accidentally dropped.
public struct KluvsTextStyle {
    public let font: Font
    public let tracking: CGFloat
    public let lineSpacing: CGFloat
    /// Non-nil only where a true italic font asset exists (Headline/Title, EB Garamond). Families
    /// with no dedicated italic asset (Caption, IBM Plex Sans) fall back to a synthetic `.italic()`
    /// transform instead — see `kluvsStyle(_:feature:)`.
    public let italicFont: Font?

    public init(font: Font, tracking: CGFloat = 0, lineSpacing: CGFloat = 0, italicFont: Font? = nil) {
        self.font = font
        self.tracking = tracking
        self.lineSpacing = lineSpacing
        self.italicFont = italicFont
    }
}

/// A multi-rung family (Display / Headline / Title) — small/medium/large sizes of the same shape.
public struct KluvsTypeScale {
    public let small: KluvsTextStyle
    public let medium: KluvsTextStyle
    public let large: KluvsTextStyle

    public init(small: KluvsTextStyle, medium: KluvsTextStyle, large: KluvsTextStyle) {
        self.small = small
        self.medium = medium
        self.large = large
    }
}

/// Body only has two rungs in design-system/docs/typography.md — no "small" observed in the audit.
public struct KluvsBodyScale {
    public let large: KluvsTextStyle
    public let medium: KluvsTextStyle

    public init(large: KluvsTextStyle, medium: KluvsTextStyle) {
        self.large = large
        self.medium = medium
    }
}

extension View {
    /// Applies a Kluvs typography role. Mirrors Android's `KluvsTheme.typography.<family>.<rung>`
    /// + `TextStyle.feature()` pair as one call: pass `feature: true` for the roman->italic
    /// register flip ("the featured/current thing" — a book title, the active reading session).
    /// Valid per design-system/docs/typography.md on Headline, Title, and Caption only — passing
    /// `feature: true` on a style with no italic behavior defined is a no-op modifier stack, not
    /// a crash, but shouldn't be relied on outside those three families.
    public func kluvsStyle(_ style: KluvsTextStyle, feature: Bool = false) -> some View {
        let useSyntheticItalic = feature && style.italicFont == nil
        let resolvedFont = feature ? (style.italicFont ?? style.font) : style.font
        return self
            .font(resolvedFont)
            .tracking(style.tracking)
            .lineSpacing(style.lineSpacing)
            .italic(useSyntheticItalic)
    }
}

/// The Kluvs typography model — mirrors kluvs-mobile's Android `KluvsTypography`/
/// `KluvsTheme.typography` and design-system/docs/typography.md's family/rung/modifier model.
/// Same family/rung names as Android (`headline.medium`, `title.large`, `eyebrow`, etc.) and the
/// same pixel values — the doc's own rule is that the numeric scale is universal across
/// platforms, only *which* rungs a platform reaches for differs (mobile never uses `display`).
///
/// This is additive, not a replacement: the existing `Typography.swift` (old `kluvsPageHeading`/
/// `kluvsBodyLg`/etc. scale) is left untouched — real call sites still read it today. Migrating
/// those call sites to `KluvsTheme.typography` is separate propagation work, not part of adding
/// the model itself (same sequencing Android's `Type.kt` -> `KluvsTypography.kt` went through).
///
/// Line-heights below are converted to SwiftUI's `.lineSpacing()` (extra space added between
/// lines) as `lineHeight - fontSize`, an approximation — same "first-pass estimate, not yet a
/// cross-platform-agreed value" caveat design-system/tokens.json and Android's own
/// `KluvsTypography.kt` already carry for this same number.
extension KluvsTheme {
    public enum typography {
        // Web-only per design-system/docs/typography.md ("mobile never uses Display") — defined
        // here anyway for model completeness/parity with Android's KluvsTypography, same as there.
        public static let display = KluvsTypeScale(
            small: KluvsTextStyle(font: .ebGaramondBold(size: 48), lineSpacing: 4),
            medium: KluvsTextStyle(font: .ebGaramondBold(size: 64), lineSpacing: 4),
            large: KluvsTextStyle(font: .ebGaramondBold(size: 96), lineSpacing: 4)
        )

        public static let headline = KluvsTypeScale(
            small: KluvsTextStyle(font: .ebGaramondMedium(size: 30), lineSpacing: 6, italicFont: .ebGaramondMediumItalic(size: 30)),
            medium: KluvsTextStyle(font: .ebGaramondMedium(size: 34), lineSpacing: 6, italicFont: .ebGaramondMediumItalic(size: 34)),
            large: KluvsTextStyle(font: .ebGaramondMedium(size: 40), lineSpacing: 6, italicFont: .ebGaramondMediumItalic(size: 40))
        )

        public static let title = KluvsTypeScale(
            small: KluvsTextStyle(font: .ebGaramondMedium(size: 15), lineSpacing: 5, italicFont: .ebGaramondMediumItalic(size: 15)),
            medium: KluvsTextStyle(font: .ebGaramondMedium(size: 19), lineSpacing: 5, italicFont: .ebGaramondMediumItalic(size: 19)),
            large: KluvsTextStyle(font: .ebGaramondMedium(size: 24), lineSpacing: 6, italicFont: .ebGaramondMediumItalic(size: 24))
        )

        public static let body = KluvsBodyScale(
            large: KluvsTextStyle(font: .plexSans(size: 16), tracking: 0.5, lineSpacing: 8),
            medium: KluvsTextStyle(font: .plexSans(size: 14), tracking: 0.25, lineSpacing: 6)
        )

        // No dedicated italic IBM Plex Sans asset is bundled — `feature` on Caption falls back to
        // a synthetic `.italic()` transform via `kluvsStyle(_:feature:)`, same as Compose's
        // `FontStyle.Italic` does on Android when no matching italic FontFamily entry exists.
        public static let caption = KluvsTextStyle(font: .plexSans(size: 13), tracking: 0.2, lineSpacing: 5)

        // tracking: design-system/tokens.json typography.family.eyebrow.tracking is 0.14em —
        // converted to absolute points at this family's fixed 11pt size (0.14 * 11 = 1.54).
        // SwiftUI's `.tracking()` takes points, not em, unlike Compose's `TextUnit`. No
        // text-transform equivalent either — callers must uppercase the string themselves, same
        // as Android's RoleEyebrow-established pattern.
        public static let eyebrow = KluvsTextStyle(font: .plexSansMedium(size: 11), tracking: 1.54, lineSpacing: 5)

        public static let label = KluvsTextStyle(font: .plexSansMedium(size: 14), tracking: 0.1, lineSpacing: 4)
        public static let finePrint = KluvsTextStyle(font: .plexSans(size: 12), tracking: 0.4, lineSpacing: 4)

        // design-system/tokens.json font-family.mono — system-stack placeholder, no dedicated
        // typeface chosen yet, same status as Android's FontFamily.Monospace choice.
        public static let mono = KluvsTextStyle(font: .system(size: 13, design: .monospaced), lineSpacing: 5)
    }
}
