package com.ivangarzab.kluvs.designsystem.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.buttons.OutlinedButton
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.feature
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// design-system/docs/states.md § Fragmented Hex Grid — keep these in sync with the reference
// asset (design-system/assets/illustration-empty-hexagons.svg) and colors_and_type.css.
// Shared by every EmptyState call site today, on purpose — not yet exposed as a per-call
// parameter. If a screen ever wants its own distinct pattern instead of the incidental
// variation that already comes from differing container sizes, promote this to an optional
// `seed: Int = HEX_SEED` parameter on EmptyState() rather than editing the constant in place.
private const val HEX_SEED = 44
private const val CELL_ACTIVATION_FRACTION = 0.26f
private const val EDGE_KEEP_PROBABILITY = 0.6f
private val HEX_RADIUS = 34.dp
private const val STROKE_WIDTH_DP = 1.75f

// Safe zone as a fraction of the container, matching the reference asset's 360x620 canvas
// (safe rect (40,220)-(320,400)): the illustration must never draw under the centered text.
private const val SAFE_LEFT_FRACTION = 40f / 360f
private const val SAFE_RIGHT_FRACTION = 320f / 360f
private const val SAFE_TOP_FRACTION = 220f / 620f
private const val SAFE_BOTTOM_FRACTION = 400f / 620f

private data class HexEdge(val start: Offset, val end: Offset)

/**
 * Generates the Fragmented Hex Grid pattern for a container of the given size, using a fixed
 * seed so the layout is identical across recompositions and app launches — never regenerated
 * at runtime with a fresh random draw. Cells are activated first, then most of each active
 * cell's edges are kept together; sampling edges independently across the whole grid instead
 * produces disconnected floating lines rather than readable partial-hexagon fragments (see
 * design-system/docs/states.md for the full rationale).
 */
private fun generateHexEdges(widthPx: Float, heightPx: Float, hexRadiusPx: Float): List<HexEdge> {
    val random = Random(HEX_SEED)
    val sqrt3 = sqrt(3f)

    fun hexCenter(row: Int, col: Int): Offset {
        val x = hexRadiusPx * sqrt3 * (col + 0.5f * (row % 2))
        val y = hexRadiusPx * 1.5f * row
        return Offset(x, y)
    }

    fun hexVertices(center: Offset): List<Offset> = (0 until 6).map { i ->
        val angle = Math.toRadians((-90 + 60 * i).toDouble())
        Offset(
            x = center.x + hexRadiusPx * cos(angle).toFloat(),
            y = center.y + hexRadiusPx * sin(angle).toFloat(),
        )
    }

    val safeLeft = widthPx * SAFE_LEFT_FRACTION
    val safeRight = widthPx * SAFE_RIGHT_FRACTION
    val safeTop = heightPx * SAFE_TOP_FRACTION
    val safeBottom = heightPx * SAFE_BOTTOM_FRACTION

    fun touchesSafeZone(p1: Offset, p2: Offset): Boolean =
        listOf(p1, p2).any { it.x in safeLeft..safeRight && it.y in safeTop..safeBottom }

    val rows = (heightPx / (hexRadiusPx * 1.5f)).toInt() + 3
    val cols = (widthPx / (hexRadiusPx * sqrt3)).toInt() + 3

    data class Cell(val center: Offset, val edges: List<Pair<Offset, Offset>>)

    val cells = mutableListOf<Cell>()
    for (r in -1 until rows) {
        for (c in -1 until cols) {
            val center = hexCenter(r, c)
            if (center.x < -hexRadiusPx || center.x > widthPx + hexRadiusPx ||
                center.y < -hexRadiusPx || center.y > heightPx + hexRadiusPx
            ) continue
            val verts = hexVertices(center)
            val edges = (0 until 6).map { i -> verts[i] to verts[(i + 1) % 6] }
            cells.add(Cell(center, edges))
        }
    }

    val eligible = cells.filter { cell ->
        !(cell.center.x > safeLeft - hexRadiusPx && cell.center.x < safeRight + hexRadiusPx &&
            cell.center.y > safeTop - hexRadiusPx && cell.center.y < safeBottom + hexRadiusPx)
    }.shuffled(random)

    val active = eligible.take((eligible.size * CELL_ACTIVATION_FRACTION).toInt())

    val kept = mutableListOf<HexEdge>()
    for (cell in active) {
        for ((p1, p2) in cell.edges) {
            if (touchesSafeZone(p1, p2)) continue
            if (random.nextFloat() < EDGE_KEEP_PROBABILITY) {
                kept.add(HexEdge(p1, p2))
            }
        }
    }
    return kept
}

/**
 * The reusable "nothing here" state — design-system/docs/states.md § Empty State. One
 * illustration (Fragmented Hex Grid) reused everywhere; only heading, body, and the optional
 * [action] slot change per screen. [action] is left null for states with no real next step
 * (e.g. "no search results yet") rather than filled with a button that goes nowhere useful.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun EmptyState(
    heading: String,
    body: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    val strokeColor = KluvsTheme.colors.disabled
    val density = LocalDensity.current

    // Sizing is entirely the caller's responsibility via [modifier] — fillMaxSize() for a
    // full-screen empty state, a bounded height for a section-level one (e.g. inside a tab
    // that already has content above it).
    BoxWithConstraints(modifier = modifier) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val hexRadiusPx = with(density) { HEX_RADIUS.toPx() }
        val strokeWidthPx = with(density) { STROKE_WIDTH_DP.dp.toPx() }
        val edges = remember(widthPx, heightPx) { generateHexEdges(widthPx, heightPx, hexRadiusPx) }

        Canvas(modifier = Modifier.fillMaxSize()) {
            edges.forEach { edge ->
                drawLine(
                    color = strokeColor,
                    start = edge.start,
                    end = edge.end,
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = heading,
                style = KluvsTheme.typography.title.large.feature(),
                color = KluvsTheme.colors.labelVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = body,
                style = KluvsTheme.typography.body.medium,
                color = KluvsTheme.colors.contentMuted,
                textAlign = TextAlign.Center,
            )
            if (action != null) {
                Spacer(modifier = Modifier.height(16.dp))
                action()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_EmptyState_NoAction() = KluvsTheme {
    EmptyState(
        modifier = Modifier.fillMaxSize().background(KluvsTheme.colors.background),
        heading = "Nothing shelved yet.",
        body = "Search for a book and add it to Want to Read, Read, or Not Finished.",
    )
}

@PreviewLightDark
@Composable
private fun Preview_EmptyState_WithAction() = KluvsTheme {
    EmptyState(
        modifier = Modifier.fillMaxSize().background(KluvsTheme.colors.background),
        heading = "No clubs yet.",
        body = "Join a club or start your own to get things going.",
        action = { OutlinedButton(text = "Join with a code", onClick = {}) },
    )
}
