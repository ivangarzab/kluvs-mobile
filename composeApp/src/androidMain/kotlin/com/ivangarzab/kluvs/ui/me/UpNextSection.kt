package com.ivangarzab.kluvs.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.member.presentation.UpNextItem
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.feature

/**
 * "Up Next" section: the nearest upcoming discussion across all of the
 * member's clubs. Flat section matching the rest of the Me screen — no card
 * fill/border. Read-only; attendance/RSVP is a separate ticket. Renders
 * nothing when there's no upcoming discussion.
 */
@Composable
fun UpNextSection(
    modifier: Modifier = Modifier,
    upNext: UpNextItem?,
) {
    if (upNext == null) return

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.up_next_eyebrow).uppercase(),
                color = KluvsTheme.colors.accent,
                style = KluvsTheme.typography.eyebrow
            )
            Text(
                text = upNext.date,
                color = KluvsTheme.colors.accent,
                style = KluvsTheme.typography.caption
            )
        }

        Text(
            text = upNext.title,
            color = KluvsTheme.colors.content,
            style = KluvsTheme.typography.title.large.feature()
        )

        Text(
            text = listOfNotNull(upNext.clubName, upNext.location).joinToString(" — "),
            color = KluvsTheme.colors.contentMuted,
            style = KluvsTheme.typography.body.medium
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_UpNextSection() = KluvsTheme {
    UpNextSection(
        modifier = Modifier.background(color = KluvsTheme.colors.background),
        upNext = UpNextItem(
            title = "End-of-Year Check-in",
            clubName = "Showcase Kluv",
            location = "Online",
            date = "December 31, 2026"
        )
    )
}
