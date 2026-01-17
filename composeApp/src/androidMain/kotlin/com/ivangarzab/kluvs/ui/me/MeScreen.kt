package com.ivangarzab.kluvs.ui.me

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.presentation.models.CurrentlyReadingBook
import com.ivangarzab.kluvs.presentation.models.UserProfile
import com.ivangarzab.kluvs.presentation.models.UserStatistics
import com.ivangarzab.kluvs.presentation.viewmodels.member.MeState
import com.ivangarzab.kluvs.presentation.viewmodels.member.MeViewModel
import com.ivangarzab.kluvs.theme.KluvsTheme
import com.ivangarzab.kluvs.ui.components.ErrorScreen
import com.ivangarzab.kluvs.ui.components.LoadingScreen
import com.ivangarzab.kluvs.presentation.models.ScreenState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MeScreen(
    modifier: Modifier = Modifier,
    userId: String,
    viewModel: MeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserData(userId)
    }

    MeScreenContent(
        modifier = modifier,
        state = state,
        onRetry = viewModel::refresh,
        onSettingsClick = { /* TODO() */ },
        onHelpClick = { /* TODO() */ },
        onSignOutClick = viewModel::signOut,
    )
}

@Composable
fun MeScreenContent(
    modifier: Modifier = Modifier,
    state: MeState,
    onRetry: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    val screenState = when {
        state.isLoading -> ScreenState.Loading
        state.error != null -> ScreenState.Error(state.error!!)
        else -> ScreenState.Content
    }

    AnimatedContent(
        targetState = screenState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "MeScreenTransition"
    ) { targetState ->
        when (targetState) {
            is ScreenState.Loading -> LoadingScreen()
            is ScreenState.Error -> ErrorScreen(
                message = targetState.message,
                onRetry = onRetry
            )
            is ScreenState.Empty,
            is ScreenState.Content -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    ProfileSection(
                        imageUrl = state.profile?.avatarUrl ?: "",
                        name = state.profile?.name ?: "",
                        handle = state.profile?.handle ?: "",
                        joinDate = state.profile?.joinDate ?: ""
                    )

                    Divider()

                    StatisticsSection(
                        modifier = Modifier.fillMaxWidth(),
                        data = state.statistics
                    )

                    Divider()

                    CurrentlyReadingSection(
                        modifier = Modifier.fillMaxWidth(),
                        currentReadings = state.currentlyReading
                    )

                    Divider()

                    FooterSection(
                        modifier = Modifier.fillMaxWidth(),
                        onSettingsClick = onSettingsClick,
                        onHelpClick = onHelpClick,
                        onSignOutClick = onSignOutClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    modifier: Modifier = Modifier,
    imageUrl: String,
    name: String,
    handle: String,
    joinDate: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            Spacer(Modifier.padding(8.dp))

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
                Text(
                    text = stringResource(R.string.member_since_x, joinDate),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FooterSection(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            FooterItem(
                label = stringResource(R.string.settings),
                icon = R.drawable.ic_settings,
                onClick = onSettingsClick
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface
            )

            FooterItem(
                label = stringResource(R.string.help_and_support),
                icon = R.drawable.ic_help,
                onClick = onHelpClick
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface
            )

            FooterItem(
                label = stringResource(R.string.sign_out),
                icon = R.drawable.ic_signout,
                iconColor = MaterialTheme.colorScheme.error,
                onClick = onSignOutClick
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.version_x, "0.0.1"),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun FooterItem(
    modifier: Modifier = Modifier,
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    @DrawableRes icon: Int,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconColor
        )
        Spacer(Modifier.padding(horizontal = 4.dp))
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun Divider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    HorizontalDivider(modifier = modifier, color = color)
}

@PreviewLightDark
@Composable
fun Preview_MeScreen() = KluvsTheme {
    MeScreenContent(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        state = MeState(
            isLoading = false,
            profile = UserProfile(
                memberId = "0",
                name = "Quill",
                handle = "@quill-bot",
                joinDate = "2025",
                avatarUrl = null
            ),
            statistics = UserStatistics(clubsCount = 6, totalPoints = 100, booksRead = 2),
            currentlyReading = listOf(
                CurrentlyReadingBook(bookTitle = "1984", clubName = "Quill's Club", progress = 0.66f, dueDate = "Tomorrow")
            )
        ),
        onRetry = { },
        onSettingsClick = { },
        onHelpClick = { },
        onSignOutClick = { },
    )
}