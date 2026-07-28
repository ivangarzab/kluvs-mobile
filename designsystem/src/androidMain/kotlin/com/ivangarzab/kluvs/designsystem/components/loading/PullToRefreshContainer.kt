package com.ivangarzab.kluvs.designsystem.components.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Kluvs-branded pull-to-refresh wrapper — thin skin over Material3's [PullToRefreshBox]
 * that replaces M3's stock arrow entirely with the brand [LoadingSpinner] mark, backed by
 * a circular card-color surface. The mark scales/fades in with [PullToRefreshState.distanceFraction]
 * as the user drags, so it's present from the start of the gesture — no swap from a stock
 * indicator to the branded one partway through.
 *
 * [isLoading] is the screen's raw loading flag (also true for non-gesture loads,
 * e.g. a `LaunchedEffect` re-firing when the screen remounts after a tab switch).
 * The pull indicator must NOT react to those — it only tracks whether a
 * user-initiated pull is in flight, via an internal flag that's set the moment
 * [onRefresh] fires from an actual drag gesture and cleared once [isLoading]
 * reports the resulting load has finished.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var isPulling by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (!isLoading) isPulling = false
    }

    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isPulling,
        onRefresh = {
            isPulling = true
            onRefresh()
        },
        state = state,
        modifier = modifier,
        indicator = {
            val progress = state.distanceFraction.coerceIn(0f, 1f)
            if (isPulling || progress > 0f) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .scale(progress)
                        .alpha(progress),
                    shape = CircleShape,
                    color = KluvsTheme.colors.card,
                    shadowElevation = 4.dp,
                ) {
                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        LoadingSpinner(size = 28.dp)
                    }
                }
            }
        },
        content = content,
    )
}
