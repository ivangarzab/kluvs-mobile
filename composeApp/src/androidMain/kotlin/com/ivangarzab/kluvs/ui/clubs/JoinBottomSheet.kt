package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.loading.LoadingSpinner
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.join.presentation.JoinState
import com.ivangarzab.kluvs.join.presentation.JoinViewModel
import com.ivangarzab.kluvs.model.ClubPreview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Bottom sheet for joining a club by invite code — the DS "fielded flow" home for what used
 * to be a full-screen navigation (see [com.ivangarzab.kluvs.ui.join.JoinScreen]'s doc comment
 * for why that screen still exists separately). Only handles the already-signed-in path
 * through [JoinViewModel] (preview a token, then join) — reachable exclusively from the
 * authenticated Clubs tab, so [JoinState.needsSignIn] can never actually fire here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinBottomSheet(
    onDismiss: () -> Unit,
    onJoined: (clubId: String) -> Unit,
    viewModel: JoinViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.joinedClubId) {
        state.joinedClubId?.let { clubId ->
            viewModel.onConsumeJoinedClubId()
            onJoined(clubId)
        }
    }

    JoinBottomSheetContent(
        state = state,
        onDismiss = onDismiss,
        onTokenChanged = viewModel::onTokenChanged,
        onPreviewInvite = viewModel::previewInvite,
        onJoinClicked = viewModel::onJoinClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinBottomSheetContent(
    state: JoinState,
    onDismiss: () -> Unit,
    onTokenChanged: (String) -> Unit,
    onPreviewInvite: () -> Unit,
    onJoinClicked: () -> Unit,
) {
    val hasPreview = state.preview != null

    BottomSheet(
        header = "Join with a Code",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = when {
                    !hasPreview -> "Preview"
                    state.isJoining -> "Joining..."
                    else -> "Join"
                },
                onAction = if (hasPreview) onJoinClicked else onPreviewInvite,
                onCancel = onDismiss,
                actionEnabled = if (hasPreview) {
                    !state.isJoining
                } else {
                    state.tokenInput.isNotBlank() && !state.isLoadingPreview
                },
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InputField(
                value = state.tokenInput,
                onValueChange = onTokenChanged,
                label = "Invite code",
                modifier = Modifier.fillMaxWidth(),
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
            }

            state.joinError?.let { error ->
                Text(
                    text = error,
                    style = KluvsTheme.typography.body.medium,
                    color = KluvsTheme.colors.danger
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_JoinBottomSheetContent_Initial() = KluvsTheme {
    JoinBottomSheetContent(
        state = JoinState(),
        onDismiss = {},
        onTokenChanged = {},
        onPreviewInvite = {},
        onJoinClicked = {},
    )
}

@PreviewLightDark
@Composable
private fun Preview_JoinBottomSheetContent_WithPreview() = KluvsTheme {
    JoinBottomSheetContent(
        state = JoinState(
            tokenInput = "abc123",
            preview = ClubPreview(id = "club-1", name = "Weird Fiction Club")
        ),
        onDismiss = {},
        onTokenChanged = {},
        onPreviewInvite = {},
        onJoinClicked = {},
    )
}
