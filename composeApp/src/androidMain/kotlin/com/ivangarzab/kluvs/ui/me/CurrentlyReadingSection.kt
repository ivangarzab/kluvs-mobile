package com.ivangarzab.kluvs.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.presentation.models.CurrentlyReadingBook
import com.ivangarzab.kluvs.theme.KluvsTheme

@Composable
fun CurrentlyReadingSection(
    modifier: Modifier = Modifier,
    currentReadings: List<CurrentlyReadingBook>,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.currently_reading),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.padding(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                currentReadings.forEachIndexed { index, reading ->
                    if (index > 2) return@Column
                    CurrentlyReadingItem(title = reading.bookTitle, progress = reading.progress)
                }
            }

            Spacer(Modifier.padding(8.dp))

            //TODO: Make this section expandable later
            if (currentReadings.size > 2) {
                Text(
                    text = stringResource(R.string.and_more),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun CurrentlyReadingItem(
    modifier: Modifier = Modifier,
    title: String,
    progress: Float,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.padding(4.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = { progress },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            gapSize = 0.dp,
            drawStopIndicator = { }
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview_CurrentlyReadingItem() = KluvsTheme {
    CurrentlyReadingItem(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        title = "1984",
        progress = 0.69f
    )
}

@PreviewLightDark
@Composable
private fun Preview_CurrentlyReadingSection() = KluvsTheme {
    CurrentlyReadingSection(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        currentReadings = listOf(
            CurrentlyReadingBook(bookTitle = "The Myth of Sisyphus", clubName = "Quill's Bookclub", progress = 0.25f, dueDate = "Tomorrow"),
            CurrentlyReadingBook(bookTitle = "Pachita", clubName = "Another Bookclub", progress = 0.66f, dueDate = "December 31st, 2026"),
            CurrentlyReadingBook(bookTitle = "1984", clubName = "Third Bookclub", progress = 0.10f, dueDate = "December 31st, 2027"),
            CurrentlyReadingBook(bookTitle = "The Philosopher Queens", clubName = "Hidden Bookclub", progress = 0.0f, dueDate = "December 31st, 2028")
        )
    )
}