package com.ivangarzab.kluvs.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ivangarzab.kluvs.designsystem.components.buttons.PrimaryButton
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Full-screen "couldn't load this" state — reuses [EmptyState]'s Fragmented Hex Grid shell with
 * [KluvsTheme.colors.danger] line color so a load failure reads as a distinct signal from an
 * empty-but-healthy screen, rather than a floating text+button popup.
 */
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    EmptyState(
        modifier = modifier.fillMaxSize(),
        heading = "Something went wrong.",
        body = message,
        lineColor = KluvsTheme.colors.danger,
        action = { PrimaryButton(text = "Retry", onClick = onRetry) },
    )
}

@PreviewLightDark
@Composable
private fun Preview_ErrorScreen() = KluvsTheme {
    ErrorScreen(
        modifier = Modifier.background(color = KluvsTheme.colors.background),
        message = "We couldn't load your clubs. Check your connection and try again.",
        onRetry = { }
    )
}
