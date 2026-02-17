package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.clubs.presentation.ClubDetailsState
import com.ivangarzab.kluvs.clubs.presentation.ClubDetailsViewModel
import com.ivangarzab.kluvs.presentation.state.ScreenState
import com.ivangarzab.kluvs.theme.KluvsTheme
import com.ivangarzab.kluvs.ui.components.ErrorScreen
import com.ivangarzab.kluvs.ui.components.LoadingScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClubsScreen(
    modifier: Modifier = Modifier,
    userId: String,
    viewModel: ClubDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserClubs(userId)
    }

    ClubsScreenContent(
        modifier = modifier,
        state = state,
        onRetry = viewModel::refresh,
        onClubSelected = viewModel::selectClub
    )
}

@Composable
fun ClubsScreenContent(
    modifier: Modifier = Modifier,
    state: ClubDetailsState,
    onRetry: () -> Unit,
    onClubSelected: (String) -> Unit = {},
) {
    val screenState = when {
        state.isLoading && state.availableClubs.isEmpty() -> ScreenState.Loading
        state.error != null && state.availableClubs.isEmpty() -> ScreenState.Error(state.error!!)
        state.availableClubs.isEmpty() && !state.isLoading -> ScreenState.Empty
        else -> ScreenState.Content
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = screenState,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "ClubsScreenTransition"
    ) { targetState ->
        when (targetState) {
            is ScreenState.Loading -> LoadingScreen()
            is ScreenState.Error -> ErrorScreen(
                message = targetState.message,
                onRetry = onRetry
            )
            is ScreenState.Empty -> {
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
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Join a club to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is ScreenState.Content -> {
                val scope = rememberCoroutineScope()
                val pagerState = rememberPagerState(
                    pageCount = { 3 },
                    initialPage = 0
                )
                val tabs = listOf(
                    stringResource(R.string.general),
                    stringResource(R.string.active_session),
                    stringResource(R.string.members)
                )

                Column(modifier = modifier) {
                    ClubSelectorRow(
                        clubName = state.currentClubDetails?.clubName ?: "",
                        hasMultipleClubs = state.availableClubs.size > 1,
                        onSelectorClick = { showBottomSheet = true }
                    )

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(title) }
                            )
                        }
                    }

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val tabModifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)

                        // Swipeable tab content
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> GeneralTab(tabModifier, state.currentClubDetails)
                                1 -> ActiveSessionTab(tabModifier, state.activeSession)
                                2 -> MembersTab(tabModifier, state.members)
                            }
                        }
                    }
                }

                if (showBottomSheet) {
                    ClubSelectorBottomSheet(
                        clubs = state.availableClubs,
                        selectedClubId = state.selectedClubId,
                        onClubSelected = { clubId ->
                            onClubSelected(clubId)
                            showBottomSheet = false
                        },
                        onDismiss = { showBottomSheet = false }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_ClubsScreen() = KluvsTheme {
    ClubsScreenContent(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        state = ClubDetailsState(
            isLoading = false
        ),
        onRetry = { }
    )
}
