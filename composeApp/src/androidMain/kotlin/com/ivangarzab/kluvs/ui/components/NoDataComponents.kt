package com.ivangarzab.kluvs.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NoTabData(
    modifier: Modifier = Modifier,
    @StringRes text: Int
) {
    Text(
        modifier = modifier,
        text = stringResource(text),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.titleMedium,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun NoSectionData(
    modifier: Modifier = Modifier,
    @StringRes text: Int
) {
    Text(
        modifier = modifier,
        text = stringResource(text),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
    )
}