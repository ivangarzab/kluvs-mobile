package com.ivangarzab.kluvs.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * The design-system-branded snackbar content — pass as `SnackbarHost(hostState, snackbar = { KluvsSnackbar(it) })`
 * so existing [androidx.compose.material3.SnackbarHostState] plumbing (show/dismiss/queueing)
 * is untouched; only the rendered content changes. See design-system/docs/states.md § Snackbar.
 *
 * Every variant uses a fully opaque fill with white (`onAccent`) text/icon — the same
 * "white on saturated brand/status surface" token PrimaryButton uses for copper — not a
 * translucent wash like ErrorBanner's `dangerSubtle`. A subtle tint is legible sitting on a
 * known, fixed background; a snackbar floats over arbitrary content and needs to read clearly
 * regardless of what's behind it.
 */
@Composable
fun KluvsSnackbar(data: SnackbarData) {
    val variant = (data.visuals as? KluvsSnackbarVisuals)?.variant ?: SnackbarVariant.SUCCESS

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 400.dp)
            .height(54.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = variant.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            type = variant.icon,
            contentDescription = null,
            tint = KluvsTheme.colors.onAccent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = data.visuals.message,
            color = KluvsTheme.colors.onAccent,
            style = KluvsTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_KluvsSnackbar_Success() = KluvsTheme {
    KluvsSnackbar(PreviewSnackbarData(
        KluvsSnackbarVisuals(
            message = "Club created",
            variant = SnackbarVariant.SUCCESS
        )
    ))
}

@PreviewLightDark
@Composable
private fun Preview_KluvsSnackbar_Danger() = KluvsTheme {
    KluvsSnackbar(
        PreviewSnackbarData(
            KluvsSnackbarVisuals(
                message = "Couldn't save changes. Please try again.",
                variant = SnackbarVariant.DANGER,
            )
        )
    )
}

private class PreviewSnackbarData(override val visuals: SnackbarVisuals) : SnackbarData {
    override fun performAction() {}
    override fun dismiss() {}
}

/**
 * Which of the design-system-approved looks a snackbar renders in — see
 * design-system/docs/states.md § Snackbar. Each variant owns its own styling below
 * ([backgroundColor], [icon]) so adding a new case later means adding one branch to each of
 * those two, not threading a new boolean through every property in [KluvsSnackbar].
 */
enum class SnackbarVariant { SUCCESS, DANGER }

/** The solid fill color for this variant's snackbar surface. */
private val SnackbarVariant.backgroundColor: Color
    @Composable get() = when (this) {
        SnackbarVariant.SUCCESS -> KluvsTheme.colors.success
        SnackbarVariant.DANGER -> KluvsTheme.colors.danger
    }

/** The leading icon for this variant. */
private val SnackbarVariant.icon: IconType
    get() = when (this) {
        SnackbarVariant.SUCCESS -> IconType.Check
        SnackbarVariant.DANGER -> IconType.Error
    }

/**
 * [SnackbarVisuals] carrying [variant] alongside the standard message/duration fields, so a
 * single [androidx.compose.material3.SnackbarHostState] can show both success and failure
 * feedback. Pass this to `SnackbarHostState.showSnackbar(visuals = ...)` instead of the
 * string-only overload whenever the danger look is needed. No action slot — nothing in this app
 * currently needs one; [SnackbarVisuals.actionLabel] is fixed to null rather than exposed as a
 * constructor param.
 */
data class KluvsSnackbarVisuals(
    override val message: String,
    val variant: SnackbarVariant = SnackbarVariant.SUCCESS,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals {
    override val actionLabel: String? = null
}
