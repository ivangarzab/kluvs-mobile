package com.ivangarzab.kluvs.designsystem.components.loading

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Kluvs-branded pull-to-refresh wrapper — thin skin over Material3's
 * [PullToRefreshBox] using the brand copper accent on the indicator instead
 * of M3's default color.
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
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isPulling,
                state = state,
                color = KluvsTheme.colors.accent,
            )
        },
        content = content,
    )
}
