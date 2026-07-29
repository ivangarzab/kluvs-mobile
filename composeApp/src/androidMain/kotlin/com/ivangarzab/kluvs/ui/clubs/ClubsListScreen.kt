package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.clubs.presentation.ClubDetailsState
import com.ivangarzab.kluvs.clubs.presentation.ClubListItem
import com.ivangarzab.kluvs.clubs.presentation.MemberAvatarInfo
import com.ivangarzab.kluvs.model.Role
import com.ivangarzab.kluvs.presentation.state.ScreenState
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.brandOnPrimary
import com.ivangarzab.kluvs.designsystem.theme.brandPrimary
import com.ivangarzab.kluvs.designsystem.theme.feature
import com.ivangarzab.kluvs.designsystem.components.bookcover.BookCoverPlaceholder
import com.ivangarzab.kluvs.designsystem.components.avatars.AvatarStack
import com.ivangarzab.kluvs.designsystem.components.avatars.AvatarStackMember
import com.ivangarzab.kluvs.designsystem.components.appbars.TopAppBar
import com.ivangarzab.kluvs.designsystem.components.ErrorScreen
import com.ivangarzab.kluvs.designsystem.components.buttons.OutlinedButton
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.loading.PullToRefreshContainer
import com.ivangarzab.kluvs.ui.components.RoleEyebrow

/**
 * Entry-point list of the member's clubs — mirrors web's `/clubs` page. Tapping a row
 * pushes the club detail screen ([ClubsScreenContent]). The FAB opens
 * [CreateClubBottomSheet] (mirrors web's "+ New" action).
 */
@Composable
fun ClubsListScreen(
    modifier: Modifier = Modifier,
    state: ClubDetailsState,
    screenState: ScreenState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit = onRetry,
    onClubSelected: (String) -> Unit,
    onAddClub: () -> Unit = {},
    onJoinWithCode: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            header = stringResource(R.string.your_eyebrow),
            title = stringResource(R.string.clubs),
            action = {
                if (screenState !is ScreenState.Empty) {
                    OutlinedButton(text = "Join with a code", onClick = onJoinWithCode)
                }
            },
        )

        Box(modifier = Modifier.weight(1f)) {
            when (screenState) {
                is ScreenState.Loading -> ClubsListSkeleton(modifier = Modifier.fillMaxSize())

                is ScreenState.Error -> ErrorScreen(
                    message = screenState.message,
                    onRetry = onRetry
                )

                is ScreenState.Empty -> ClubsListEmptyState(
                    modifier = Modifier.fillMaxSize(),
                    onJoinWithCode = onJoinWithCode
                )

                is ScreenState.Content -> PullToRefreshContainer(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(state.availableClubs) { club ->
                            ClubListRow(
                                club = club,
                                onClick = { onClubSelected(club.id) }
                            )
                            HorizontalDivider(color = KluvsTheme.colors.cardAlt)
                        }
                    }
                }
            }

            if (screenState is ScreenState.Content || screenState is ScreenState.Empty) {
                FloatingActionButton(
                    onClick = onAddClub,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(18.dp),
                    containerColor = brandPrimary,
                    contentColor = brandOnPrimary
                ) {
                    Icon(
                        type = IconType.Add,
                        contentDescription = "New club"
                    )
                }
            }
        }
    }
}

@Composable
private fun ClubListRow(
    club: ClubListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = club.bookCoverUrl,
            contentDescription = club.bookTitle,
            modifier = Modifier
                .width(40.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            loading = { BookCoverPlaceholder(modifier = Modifier.fillMaxWidth()) },
            error = { BookCoverPlaceholder(modifier = Modifier.fillMaxWidth()) }
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = club.name,
                    style = KluvsTheme.typography.title.medium,
                    color = KluvsTheme.colors.content
                )
                club.role?.let { RoleEyebrow(role = it) }
            }

            club.bookTitle?.let { title ->
                Text(
                    text = title,
                    style = KluvsTheme.typography.title.small.feature(),
                    color = KluvsTheme.colors.contentMuted
                )
            }

            if (club.memberAvatarUrls.isNotEmpty()) {
                AvatarStack(
                    members = club.memberAvatarUrls.map { it.toAvatarStackMember() },
                    size = 20.dp
                )
            }
        }
        Icon(
            type = IconType.ChevronRight,
            contentDescription = null,
            tint = KluvsTheme.colors.contentMuted
        )
    }
}

private fun MemberAvatarInfo.toAvatarStackMember() = AvatarStackMember(
    id = memberId,
    name = name,
    avatarUrl = avatarUrl
)

@Composable
private fun ClubsListEmptyState(modifier: Modifier = Modifier, onJoinWithCode: () -> Unit = {}) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No clubs yet",
                style = KluvsTheme.typography.headline.small,
                color = KluvsTheme.colors.content
            )
            Text(
                text = "Join a club to get started",
                style = KluvsTheme.typography.body.medium,
                color = KluvsTheme.colors.contentMuted
            )
            OutlinedButton(text = "Join with a code", onClick = onJoinWithCode)
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_ClubsListScreen() = KluvsTheme {
    ClubsListScreen(
        modifier = Modifier.fillMaxSize(),
        state = ClubDetailsState(
            availableClubs = listOf(
                ClubListItem(
                    id = "1",
                    name = "Weird Fiction Club",
                    role = Role.OWNER,
                    bookTitle = "Piranesi",
                    bookCoverUrl = null,
                    memberAvatarUrls = listOf(
                        MemberAvatarInfo("a", "Ana Silva", null),
                        MemberAvatarInfo("b", "Ben Choi", null),
                        MemberAvatarInfo("c", "Cara Doyle", null),
                        MemberAvatarInfo("d", "Dev Patel", null),
                    ),
                    memberCount = 4
                ),
                ClubListItem(id = "2", name = "Nonfiction Nook", role = Role.ADMIN),
                ClubListItem(id = "3", name = "Babel Book Bar", role = Role.MEMBER),
            )
        ),
        screenState = ScreenState.Content,
        onRetry = {},
        onClubSelected = {}
    )
}
