package com.ivangarzab.kluvs.designsystem.components.buttons

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Icon-only button with a bordered container — the icon-only counterpart to [OutlinedButton]'s
 * grey/muted outline, sharing its 12dp radius rather than [com.ivangarzab.kluvs.designsystem.components.pills.TogglePill]'s
 * circular shape (that one's reserved for persistent binary toggles, not one-shot actions).
 */
@Composable
fun OutlinedIconButton(
    type: IconType,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = KluvsTheme.colors.contentMuted,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .border(1.dp, KluvsTheme.colors.divider, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(type = type, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@PreviewLightDark
@Composable
private fun Preview_OutlinedIconButton() = KluvsTheme {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedIconButton(type = IconType.Search, contentDescription = "Search", onClick = {})
        OutlinedIconButton(type = IconType.Search, contentDescription = "Search", onClick = {}, enabled = false)
    }
}
