package com.ivangarzab.kluvs.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.settings.presentation.EditableProfile
import com.ivangarzab.kluvs.settings.presentation.SettingsState
import com.ivangarzab.kluvs.settings.presentation.SettingsViewModel
import com.ivangarzab.kluvs.designsystem.components.appbars.TopAppBar
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.ui.utils.compressImage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val bytes = compressImage(inputStream.readBytes())
                viewModel.uploadAvatar(bytes)
            }
        }
    }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar(context.getString(R.string.save_success))
            viewModel.onDismissSaveSuccess()
        }
    }

    LaunchedEffect(state.avatarError) {
        state.avatarError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearAvatarError()
        }
    }

    SettingsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onNameChanged = viewModel::onNameChanged,
        onHandleChanged = viewModel::onHandleChanged,
        onSaveProfile = viewModel::onSaveProfile,
        onAvatarClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigateBack: () -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onHandleChanged: (String) -> Unit = {},
    onSaveProfile: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                header = stringResource(R.string.settings_title),
                onNavigateBack = onNavigateBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            EditProfileSection(
                avatarUrl = state.profile?.avatarUrl,
                name = state.editedName,
                isUploadingAvatar = state.isUploadingAvatar,
                onAvatarClick = onAvatarClick,
                editedName = state.editedName,
                editedHandle = state.editedHandle,
                hasChanges = state.hasChanges,
                isSaving = state.isSaving,
                saveError = state.saveError,
                onNameChanged = onNameChanged,
                onHandleChanged = onHandleChanged,
                onSaveProfile = onSaveProfile,
            )

            LegalSection(context = context)

            AboutSection()
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_SettingsScreen() = KluvsTheme {
    SettingsScreenContent(
        state = SettingsState(
            isLoading = false,
            profile = EditableProfile(memberId = "1", name = "Alice", handle = "alice_reads"),
            editedName = "Alice",
            editedHandle = "alice_reads",
            hasChanges = false,
        )
    )
}

@PreviewLightDark
@Composable
fun Preview_SettingsScreen_WithChanges() = KluvsTheme {
    SettingsScreenContent(
        state = SettingsState(
            isLoading = false,
            profile = EditableProfile(memberId = "1", name = "Alice", handle = "alice_reads"),
            editedName = "Alice Updated",
            editedHandle = "alice_reads",
            hasChanges = true,
        )
    )
}
