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
import com.ivangarzab.kluvs.designsystem.components.buttons.TextButton
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Which of the two design-system-approved looks a snackbar renders in — see
 * design-system/docs/states.md § Snackbar.
 */
enum class SnackbarVariant { NEUTRAL, DANGER }

/**
 * [SnackbarVisuals] carrying [variant] alongside the standard message/action/duration fields, so
 * a single [androidx.compose.material3.SnackbarHostState] can show both success and failure
 * feedback. Pass this to `SnackbarHostState.showSnackbar(visuals = ...)` instead of the
 * string-only overload whenever the danger look is needed.
 */
data class KluvsSnackbarVisuals(
    override val message: String,
    val variant: SnackbarVariant = SnackbarVariant.NEUTRAL,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

/**
 * The design-system-branded snackbar content — pass as `SnackbarHost(hostState, snackbar = { KluvsSnackbar(it) })`
 * so existing [androidx.compose.material3.SnackbarHostState] plumbing (show/dismiss/queueing)
 * is untouched; only the rendered content changes. See design-system/docs/states.md § Snackbar.
 */
@Composable
fun KluvsSnackbar(data: SnackbarData) {
    val isDanger = (data.visuals as? KluvsSnackbarVisuals)?.variant == SnackbarVariant.DANGER

    // Danger uses a fully opaque fill (KluvsTheme.colors.danger) with white text/icon
    // (onAccent — the same "white on saturated brand/status surface" token PrimaryButton uses
    // for copper), not the translucent dangerSubtle wash ErrorBanner/DangerZoneBox use. A
    // subtle tint is legible sitting on a known, fixed background; a snackbar floats over
    // arbitrary content and needs to read clearly regardless of what's behind it.
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 400.dp)
            .height(54.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = if (isDanger) KluvsTheme.colors.danger else KluvsTheme.colors.bar)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isDanger) {
            Icon(
                type = IconType.Error,
                contentDescription = null,
                tint = KluvsTheme.colors.onAccent,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = data.visuals.message,
            color = if (isDanger) KluvsTheme.colors.onAccent else KluvsTheme.colors.content,
            style = KluvsTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        data.visuals.actionLabel?.let { label ->
            TextButton(text = label, onClick = { data.performAction() }, emphasized = true)
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_KluvsSnackbar_Neutral() = KluvsTheme {
    KluvsSnackbar(PreviewSnackbarData(KluvsSnackbarVisuals(message = "Club created")))
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

@PreviewLightDark
@Composable
private fun Preview_KluvsSnackbar_WithAction() = KluvsTheme {
    KluvsSnackbar(
        PreviewSnackbarData(
            KluvsSnackbarVisuals(message = "Removed from shelf", actionLabel = "Undo")
        )
    )
}

private class PreviewSnackbarData(override val visuals: SnackbarVisuals) : SnackbarData {
    override fun performAction() {}
    override fun dismiss() {}
}
