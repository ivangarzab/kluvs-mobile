package com.ivangarzab.kluvs.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.feature

/**
 * Auth-screen masthead — brand mark, voice-phrase headline (italic, "the featured thing" per
 * design-system/docs/typography.md's `feature` modifier), and an optional subhead. Shared across
 * sign in, sign up, and forgot-password so the voice stays consistent across the auth flow.
 */
@Composable
fun AuthMasthead(
    voicePhrase: String,
    modifier: Modifier = Modifier,
    subhead: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_kluvs_mark),
            contentDescription = null,
            modifier = Modifier.height(48.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = voicePhrase,
            color = KluvsTheme.colors.content,
            style = KluvsTheme.typography.headline.small.feature(),
            textAlign = TextAlign.Center,
        )

        if (subhead != null) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subhead,
                color = KluvsTheme.colors.contentMuted,
                style = KluvsTheme.typography.body.medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_AuthMasthead() = KluvsTheme {
    Column(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(24.dp),
    ) {
        AuthMasthead(
            voicePhrase = "Welcome back",
            subhead = "Sign in to keep reading together.",
        )
    }
}
