package com.ivangarzab.kluvs.designsystem.components.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * One pulsing color shared by every "bone" in a skeleton screen — call this once per skeleton
 * and reuse the returned [Color] across all its placeholder shapes so they all pulse in sync,
 * rather than calling this per-bone (which would desync their phases, since each
 * [rememberInfiniteTransition] starts its own clock at first composition).
 */
@Composable
fun rememberShimmerColor(): Color {
    val transition = rememberInfiniteTransition(label = "Shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShimmerAlpha"
    )
    return KluvsTheme.colors.contentMuted.copy(alpha = alpha)
}
