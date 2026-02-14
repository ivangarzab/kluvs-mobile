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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.clubs.presentation.MemberListItemInfo
import com.ivangarzab.kluvs.theme.KluvsTheme
import com.ivangarzab.kluvs.ui.components.MemberAvatar
import com.ivangarzab.kluvs.ui.components.NoTabData

@Composable
fun MembersTab(
    modifier: Modifier = Modifier,
    members: List<MemberListItemInfo>
) {
    if (members.isEmpty()) {
        NoTabData(
            modifier = modifier,
            text = R.string.no_members_in_club
        )
        return
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.members_x, members.size),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn {
                itemsIndexed(members) { index, member ->
                    MemberListItem(
                        name = member.name,
                        handle = member.handle,
                        avatarUrl = member.avatarUrl
                    )
                    if (index < members.size - 1) {
                        MemberDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberListItem(
    modifier: Modifier = Modifier,
    name: String,
    handle: String,
    avatarUrl: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MemberAvatar(
                avatarUrl = avatarUrl,
                size = 40.dp,
                contentDescription = stringResource(R.string.avatar_of_x, name)
            )
            Column {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = handle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MemberDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
}

@PreviewLightDark
@Composable
fun Preview_MembersTab() = KluvsTheme {
    MembersTab(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        members = listOf(
            MemberListItemInfo("0", "Iván Garza Bermea", "@ivangarzab", ""),
            MemberListItemInfo("1", "Monica Michelle Morales", "@monica", ""),
            MemberListItemInfo("2", "Marco \"Chitho\" Rivera", "@chitho23", ""),
            MemberListItemInfo("3", "Anacleto \"Keto\" Longoria", "@keto92", ""),
            MemberListItemInfo("4", "Joel Oscar Julian Salinas", "@josalinas", ""),
            MemberListItemInfo("5", "Ginseng Joaquin Guzman", "gino1", ""),
        )
    )
}