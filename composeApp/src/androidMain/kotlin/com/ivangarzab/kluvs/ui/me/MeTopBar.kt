package com.ivangarzab.kluvs.ui.me

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenu
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenuItem
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Root-mode top bar for the Me tab — mirrors [com.ivangarzab.kluvs.ui.books.BooksTopBar]'s
 * layout (64dp, title left, one trailing utility icon right). Reading Log is
 * the screen's single utility action, exposed via the trailing kebab menu.
 */
@Composable
fun MeTopBar(
    modifier: Modifier = Modifier,
    onReadingLogClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.me),
            style = MaterialTheme.typography.titleLarge,
            color = KluvsTheme.colors.content
        )
        ActionMenu(
            items = listOf(
                ActionMenuItem(label = stringResource(R.string.reading_log), onClick = onReadingLogClick)
            ),
            contentDescription = stringResource(R.string.profile_menu)
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_MeTopBar() = KluvsTheme {
    MeTopBar()
}
