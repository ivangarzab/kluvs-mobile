package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.presentation.models.BookInfo
import com.ivangarzab.kluvs.presentation.models.ClubDetails
import com.ivangarzab.kluvs.presentation.models.DiscussionInfo
import com.ivangarzab.kluvs.theme.KluvsTheme
import com.ivangarzab.kluvs.ui.components.NextDiscussionCard
import com.ivangarzab.kluvs.ui.components.NoSectionData
import com.ivangarzab.kluvs.ui.components.NoTabData

@Composable
fun GeneralTab(
    modifier: Modifier = Modifier,
    clubDetails: ClubDetails? = null,
) {
    if (clubDetails == null) {
        NoTabData(
            modifier = modifier,
            text = R.string.no_club_details
        )
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Club Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = clubDetails.clubName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.x_members, clubDetails.memberCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.founded_in_x,
                        clubDetails.foundedYear ?: stringResource(R.string.na)
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        // Current Book Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.current_book),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                clubDetails.currentBook?.let { clubInfo ->
                    Text(
                        text = clubInfo.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = clubInfo.author,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = clubDetails.currentBook?.year ?: stringResource(R.string.na),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        clubDetails.currentBook?.pageCount?.let { pages ->
                            Text(
                                text = stringResource(R.string.vertical_separator),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.x_pages, pages),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } ?: NoSectionData(text = R.string.no_book_data)
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        // Next Discussion Card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.next_discussion),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                clubDetails.nextDiscussion?.let { discussion ->
                    NextDiscussionCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = discussion.title,
                        location = discussion.location,
                        formattedDate = discussion.formattedDate,
                    )
                } ?: NoSectionData(text = R.string.no_upcoming_discussion)
            }
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_GeneralTab() = KluvsTheme {
    GeneralTab(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        clubDetails = ClubDetails(
            clubId = "club1",
            clubName = "Test Club Name",
            memberCount = 6,
            foundedYear = "2026",
            currentBook = BookInfo(
                title = "1984",
                author = "George Orwell",
                year = "1948",
                pageCount = 169
            ),
            nextDiscussion = DiscussionInfo(
                title = "Discussion #1",
                location = "Discord",
                formattedDate = "Tomorrow at 7:00 PM"
            )
        )
    )
}