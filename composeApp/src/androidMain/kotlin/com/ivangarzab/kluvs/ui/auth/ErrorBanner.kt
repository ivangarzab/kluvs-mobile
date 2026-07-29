package com.ivangarzab.kluvs.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Inline auth-form error, matching kluvs-frontend's persistent ErrorBanner — replaces the
 * transient Snackbar previously used here, since form errors (invalid credentials, rate limits)
 * need to stay visible until the user acts, not fade after a few seconds.
 */
@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = KluvsTheme.colors.dangerSubtle)
            .border(width = 1.dp, color = KluvsTheme.colors.danger, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            type = IconType.Error,
            contentDescription = null,
            tint = KluvsTheme.colors.danger,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = message,
            color = KluvsTheme.colors.danger,
            style = KluvsTheme.typography.caption,
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_ErrorBanner() = KluvsTheme {
    ErrorBanner(
        message = "Invalid email or password",
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(16.dp),
    )
}
