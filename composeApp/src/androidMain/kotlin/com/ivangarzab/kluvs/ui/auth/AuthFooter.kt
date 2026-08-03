package com.ivangarzab.kluvs.ui.auth

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.feature

private const val PRIVACY_POLICY_URL = "https://kluvs.com/privacy"
private const val TERMS_OF_USE_URL = "https://kluvs.com/terms"

/**
 * Closing block for every auth screen — italic tagline + Privacy/Terms links, matching the
 * kluvs-frontend LoginPage footer. Rendered under a hairline divider so it reads as a distinct
 * closing section rather than trailing off the form above it.
 */
@Composable
fun AuthFooter(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalDivider(color = KluvsTheme.colors.divider)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.reading_done_best_together),
            color = KluvsTheme.colors.contentMuted,
            style = KluvsTheme.typography.title.small.feature(),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.privacy_policy).uppercase(),
                color = KluvsTheme.colors.contentMuted,
                style = KluvsTheme.typography.eyebrow,
                modifier = Modifier.clickable { openAuthFooterLink(context, PRIVACY_POLICY_URL) },
            )

            Spacer(modifier = Modifier.size(10.dp))
            Spacer(
                modifier = Modifier
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(color = KluvsTheme.colors.divider)
            )
            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = stringResource(R.string.terms_of_use).uppercase(),
                color = KluvsTheme.colors.contentMuted,
                style = KluvsTheme.typography.eyebrow,
                modifier = Modifier.clickable { openAuthFooterLink(context, TERMS_OF_USE_URL) },
            )
        }
    }
}

private fun openAuthFooterLink(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(context, url.toUri())
}

@PreviewLightDark
@Composable
private fun Preview_AuthFooter() = KluvsTheme {
    AuthFooter(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(24.dp),
    )
}
