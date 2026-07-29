package com.ivangarzab.kluvs.designsystem.components.loading

import android.widget.ImageView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.ivangarzab.kluvs.designsystem.R
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * The Kluvs brand loading indicator — Breathe·Tidal animated spinner (design-system
 * "Kluvs Loading Spinner", see design-system/docs/spinner-kluvs.md). Compose doesn't
 * natively loop an `AnimatedVectorDrawable`, so this wraps it in an [AndroidView].
 */
@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                val avd = AnimatedVectorDrawableCompat.create(context, R.drawable.spinner_kluvs_animation)
                setImageDrawable(avd)
                avd?.start()
            }
        },
        // spinner_kluvs.xml pads its viewport so the "inhale" scale peak never clips;
        // scale the View up to match so the drawn mark still renders at `size`.
        modifier = modifier.size(size * VIEWPORT_PADDING_FACTOR),
    )
}

// 500 / 394.41 — the padded viewport's side length over the original mark's width.
private const val VIEWPORT_PADDING_FACTOR = 1.268f

@PreviewLightDark
@Composable
private fun Preview_LoadingSpinner() = KluvsTheme {
    LoadingSpinner()
}
