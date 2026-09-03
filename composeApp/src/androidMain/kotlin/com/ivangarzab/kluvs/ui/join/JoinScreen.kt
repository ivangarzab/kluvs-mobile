package com.ivangarzab.kluvs.ui.join

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.join.presentation.JoinState
import com.ivangarzab.kluvs.join.presentation.JoinViewModel
import com.ivangarzab.kluvs.designsystem.components.buttons.IconButton
import com.ivangarzab.kluvs.designsystem.components.buttons.PrimaryButton
import com.ivangarzab.kluvs.designsystem.components.buttons.SecondaryButton
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.components.loading.LoadingSpinner
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Join-by-invite-token screen, reached by opening an invite App Link. "Join with a code" inside
 * the (already-authenticated) Clubs tab opens [com.ivangarzab.kluvs.ui.clubs.JoinBottomSheet]
 * in-place instead — a deep link needs to land somewhere before auth resolves and needs the
 * [onNeedsSignIn] → Login handoff below, neither of which a nested bottom sheet can do.
 *
 * When [initialToken] is present the code is pre-filled and previewed immediately, so a
 * recipient who tapped a link sees the club rather than an empty text field.
 *
 * The preview shows only the club name — [com.ivangarzab.kluvs.model.ClubPreview] has no
 * avatar/member-count yet (also a follow-up, needs a backend spec change).
 */
@Composable
fun JoinScreen(
    modifier: Modifier = Modifier,
    initialToken: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToClub: (clubId: String) -> Unit,
    onNeedsSignIn: (token: String) -> Unit,
    viewModel: JoinViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialToken) {
        if (!initialToken.isNullOrBlank()) {
            viewModel.onTokenChanged(initialToken)
            viewModel.previewInvite()
        }
    }

    LaunchedEffect(state.joinedClubId) {
        state.joinedClubId?.let { clubId ->
            onNavigateToClub(clubId)
            viewModel.onConsumeJoinedClubId()
        }
    }

    LaunchedEffect(state.needsSignIn) {
        if (state.needsSignIn) {
            onNeedsSignIn(state.tokenInput.trim())
            viewModel.onConsumeNeedsSignIn()
        }
    }

    JoinScreenContent(
        modifier = modifier,
        state = state,
        onNavigateBack = onNavigateBack,
        onTokenChanged = viewModel::onTokenChanged,
        onPreviewInvite = viewModel::previewInvite,
        onJoinClicked = viewModel::onJoinClicked
    )
}

@Composable
private fun JoinScreenContent(
    modifier: Modifier = Modifier,
    state: JoinState,
    onNavigateBack: () -> Unit = {},
    onTokenChanged: (String) -> Unit = {},
    onPreviewInvite: () -> Unit = {},
    onJoinClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            type = IconType.ArrowBack,
            contentDescription = stringResource(R.string.navigate_back),
            onClick = onNavigateBack,
        )

        Text(
            text = "Join a club",
            style = KluvsTheme.typography.headline.small,
            color = KluvsTheme.colors.content
        )

        InputField(
            value = state.tokenInput,
            onValueChange = onTokenChanged,
            label = "Invite code",
            modifier = Modifier.fillMaxWidth(),
        )

        SecondaryButton(
            text = "Preview",
            onClick = onPreviewInvite,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.tokenInput.isNotBlank() && !state.isLoadingPreview
        )

        if (state.isLoadingPreview) {
            LoadingSpinner()
        }

        state.previewError?.let { error ->
            Text(
                text = error,
                style = KluvsTheme.typography.body.medium,
                color = KluvsTheme.colors.danger
            )
        }

        state.preview?.let { preview ->
            Text(
                text = preview.name,
                style = KluvsTheme.typography.headline.small,
                color = KluvsTheme.colors.content
            )

            state.joinError?.let { error ->
                Text(
                    text = error,
                    style = KluvsTheme.typography.body.medium,
                    color = KluvsTheme.colors.danger
                )
            }

            PrimaryButton(
                text = if (state.isJoining) "Joining..." else "Join",
                onClick = onJoinClicked,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isJoining
            )
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_JoinScreen() = KluvsTheme {
    JoinScreenContent(
        state = JoinState(
            tokenInput = "abc123",
            preview = com.ivangarzab.kluvs.model.ClubPreview(id = "club-1", name = "Weird Fiction Club")
        )
    )
}
