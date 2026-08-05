import SwiftUI

/// Deterministic seeded PRNG (SplitMix64) — Swift's `SystemRandomNumberGenerator` cannot be
/// seeded, so a fixed-seed pattern (see `EmptyState` below) needs its own generator. Does not
/// need to produce bit-identical output to Android's `kotlin.random.Random(seed)` — each
/// platform only needs to be deterministic against itself, not against the other platform's
/// PRNG algorithm (see design-system/docs/states.md).
private struct SeededGenerator: RandomNumberGenerator {
    private var state: UInt64

    init(seed: Int) {
        state = UInt64(bitPattern: Int64(seed)) &+ 0x9E3779B97F4A7C15
    }

    mutating func next() -> UInt64 {
        state &+= 0x9E3779B97F4A7C15
        var z = state
        z = (z ^ (z >> 30)) &* 0xBF58476D1CE4E5B9
        z = (z ^ (z >> 27)) &* 0x94D049BB133111EB
        return z ^ (z >> 31)
    }
}

private struct HexEdge {
    let start: CGPoint
    let end: CGPoint
}

// design-system/docs/states.md § Fragmented Hex Grid — keep these in sync with Android's
// EmptyState.kt and the reference asset (design-system/assets/illustration-empty-hexagons.svg).
// Shared by every EmptyState call site today, on purpose — not yet exposed as a per-call
// parameter. See Android's equivalent constant for the promotion note if that ever changes.
private let hexSeed = 44
private let cellActivationFraction = 0.26
private let edgeKeepProbability = 0.6
private let hexRadius: CGFloat = 34
private let strokeWidth: CGFloat = 1.75

// Safe zone as a fraction of the container, matching the reference asset's 360x620 canvas
// (safe rect (40,220)-(320,400)): the illustration must never draw under the centered text.
private let safeLeftFraction: CGFloat = 40 / 360
private let safeRightFraction: CGFloat = 320 / 360
private let safeTopFraction: CGFloat = 220 / 620
private let safeBottomFraction: CGFloat = 400 / 620

/// Generates the Fragmented Hex Grid pattern for a container of the given size, using a fixed
/// seed so the layout is identical across renders and app launches — never regenerated at
/// runtime with a fresh random draw. Cells are activated first, then most of each active cell's
/// edges are kept together; sampling edges independently across the whole grid instead produces
/// disconnected floating lines rather than readable partial-hexagon fragments (see
/// design-system/docs/states.md for the full rationale). Mirrors Android's `generateHexEdges`.
private func generateHexEdges(width: CGFloat, height: CGFloat) -> [HexEdge] {
    var rng = SeededGenerator(seed: hexSeed)
    let sqrt3 = CGFloat(3).squareRoot()

    func hexCenter(row: Int, col: Int) -> CGPoint {
        let x = hexRadius * sqrt3 * (CGFloat(col) + 0.5 * CGFloat(row % 2))
        let y = hexRadius * 1.5 * CGFloat(row)
        return CGPoint(x: x, y: y)
    }

    func hexVertices(center: CGPoint) -> [CGPoint] {
        (0..<6).map { i in
            let angle = (-90.0 + 60.0 * Double(i)) * .pi / 180.0
            return CGPoint(
                x: center.x + hexRadius * CGFloat(cos(angle)),
                y: center.y + hexRadius * CGFloat(sin(angle))
            )
        }
    }

    let safeLeft = width * safeLeftFraction
    let safeRight = width * safeRightFraction
    let safeTop = height * safeTopFraction
    let safeBottom = height * safeBottomFraction

    func touchesSafeZone(_ p1: CGPoint, _ p2: CGPoint) -> Bool {
        [p1, p2].contains { p in
            p.x >= safeLeft && p.x <= safeRight && p.y >= safeTop && p.y <= safeBottom
        }
    }

    let rows = Int(height / (hexRadius * 1.5)) + 3
    let cols = Int(width / (hexRadius * sqrt3)) + 3

    struct Cell {
        let center: CGPoint
        let edges: [(CGPoint, CGPoint)]
    }

    var cells: [Cell] = []
    for r in -1..<rows {
        for c in -1..<cols {
            let center = hexCenter(row: r, col: c)
            if center.x < -hexRadius || center.x > width + hexRadius ||
                center.y < -hexRadius || center.y > height + hexRadius {
                continue
            }
            let verts = hexVertices(center: center)
            let edges = (0..<6).map { i in (verts[i], verts[(i + 1) % 6]) }
            cells.append(Cell(center: center, edges: edges))
        }
    }

    let eligible = cells.filter { cell in
        !(cell.center.x > safeLeft - hexRadius && cell.center.x < safeRight + hexRadius &&
            cell.center.y > safeTop - hexRadius && cell.center.y < safeBottom + hexRadius)
    }.shuffled(using: &rng)

    let active = eligible.prefix(Int(Double(eligible.count) * cellActivationFraction))

    var kept: [HexEdge] = []
    for cell in active {
        for (p1, p2) in cell.edges {
            if touchesSafeZone(p1, p2) { continue }
            if Double.random(in: 0..<1, using: &rng) < edgeKeepProbability {
                kept.append(HexEdge(start: p1, end: p2))
            }
        }
    }
    return kept
}

/// The reusable "nothing here" / "couldn't load this" state shell — design-system/docs/states.md
/// § Empty State. One illustration (Fragmented Hex Grid) reused everywhere; only heading, body,
/// `lineColor`, and the optional `action` slot change per screen. Omit `action` for states with
/// no real next step (e.g. "no search results yet") rather than filling it with a button that
/// goes nowhere useful. `lineColor` defaults to the quiet empty-state tone; `ErrorView` overrides
/// it to `danger` red so the same illustration reads as a failure, not absence. Mirrors Android's
/// `EmptyState` composable.
public struct EmptyState<Action: View>: View {
    private let heading: String
    private let bodyText: String
    private let lineColor: Color
    private let action: Action

    public init(
        heading: String,
        body: String,
        lineColor: Color = KluvsTheme.colors.disabled,
        @ViewBuilder action: () -> Action = { EmptyView() }
    ) {
        self.heading = heading
        self.bodyText = body
        self.lineColor = lineColor
        self.action = action()
    }

    public var body: some View {
        GeometryReader { geometry in
            ZStack {
                Canvas { context, size in
                    let edges = generateHexEdges(width: size.width, height: size.height)
                    var path = Path()
                    for edge in edges {
                        path.move(to: edge.start)
                        path.addLine(to: edge.end)
                    }
                    context.stroke(
                        path,
                        with: .color(lineColor),
                        style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round)
                    )
                }

                VStack(spacing: 10) {
                    Text(heading)
                        .kluvsStyle(KluvsTheme.typography.title.large, feature: true)
                        .foregroundColor(KluvsTheme.colors.labelVariant)
                        .multilineTextAlignment(.center)
                    Text(bodyText)
                        .kluvsStyle(KluvsTheme.typography.body.medium)
                        .foregroundColor(KluvsTheme.colors.contentMuted)
                        .multilineTextAlignment(.center)
                    action
                        .padding(.top, 6)
                }
                .padding(.horizontal, 40)
                .frame(width: geometry.size.width)
            }
        }
    }
}

#Preview("EmptyState — no action") {
    EmptyState(
        heading: "Nothing shelved yet.",
        body: "Search for a book and add it to Want to Read, Read, or Not Finished."
    )
    .frame(width: 360, height: 620)
    .background(KluvsTheme.colors.background)
}

#Preview("EmptyState — with action") {
    EmptyState(
        heading: "No clubs yet.",
        body: "Join a club or start your own to get things going."
    ) {
        OutlinedButton(text: "Join with a code", action: {})
    }
    .frame(width: 360, height: 620)
    .background(KluvsTheme.colors.background)
}
