package com.ivangarzab.kluvs.ui.me

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.member.presentation.UserStatistics
import com.ivangarzab.kluvs.theme.KluvsTheme

@Composable
fun StatisticsSection(
    modifier: Modifier = Modifier,
    data: UserStatistics?
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
                text = stringResource(R.string.your_statistics),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.padding(8.dp))

            StatisticsItem(
                icon = R.drawable.ic_clubs,
                label = stringResource(R.string.no_of_clubs),
                value = data?.clubsCount.let { clubsCount ->
                    if (clubsCount != null && clubsCount > 0) {
                        clubsCount.toString()
                    } else stringResource(R.string.na)
                }
            )

            Spacer(Modifier.padding(4.dp))

            StatisticsItem(
                icon = R.drawable.ic_points,
                label = stringResource(R.string.points),
                value = data?.totalPoints.let { totalPoints ->
                    if (totalPoints != null && totalPoints > 0) {
                        totalPoints.toString()
                    } else stringResource(R.string.na)
                }
            )

            Spacer(Modifier.padding(4.dp))

            StatisticsItem(
                icon = R.drawable.ic_book,
                label = stringResource(R.string.books_read),
                value = data?.booksRead.let { booksRead ->
                    if (booksRead != null && booksRead > 0) {
                        booksRead.toString()
                    } else stringResource(R.string.na)
                }
            )
        }
    }
}

@Composable
private fun StatisticsItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    label: String,
    value: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.padding(4.dp))
        Column {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_StatisticsItem() = KluvsTheme {
    StatisticsItem(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        label = "Section",
        value = "100",
        icon = R.drawable.ic_clubs
    )
}

@PreviewLightDark
@Composable
private fun Preview_StatisticsSection() = KluvsTheme {
    StatisticsSection(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        data = UserStatistics(
            clubsCount = 1,
            totalPoints = 100,
            booksRead = 2
        )
    )
}