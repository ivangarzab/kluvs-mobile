package com.ivangarzab.kluvs.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.designsystem.components.buttons.PrimaryButton
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

@Composable
fun EditProfileSection(
    modifier: Modifier = Modifier,
    editedName: String,
    editedHandle: String,
    hasChanges: Boolean,
    isSaving: Boolean,
    saveError: String?,
    onNameChanged: (String) -> Unit,
    onHandleChanged: (String) -> Unit,
    onSaveProfile: () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text = stringResource(R.string.edit_profile),
            style = KluvsTheme.typography.title.medium,
            color = KluvsTheme.colors.content
        )

        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        InputField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.label_name),
            value = editedName,
            onValueChange = onNameChanged,
        )

        Spacer(Modifier.height(8.dp))

        InputField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.label_handle),
            value = editedHandle,
            onValueChange = onHandleChanged,
            prefix = "@",
        )

        if (saveError != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = saveError,
                color = KluvsTheme.colors.danger,
                style = KluvsTheme.typography.body.medium,
            )
        }

        Spacer(Modifier.height(16.dp))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSaveProfile,
            enabled = hasChanges && !isSaving,
            text = if (isSaving) stringResource(R.string.button_save) + "…"
                else stringResource(R.string.button_save),
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_EditProfileSection() = KluvsTheme {
    EditProfileSection(
        editedName = "Alice",
        editedHandle = "alice_reads",
        hasChanges = true,
        isSaving = false,
        saveError = null,
        onNameChanged = {},
        onHandleChanged = {},
        onSaveProfile = {},
    )
}

@PreviewLightDark
@Composable
fun Preview_EditProfileSection_WithError() = KluvsTheme {
    EditProfileSection(
        editedName = "Alice",
        editedHandle = "invalid handle!",
        hasChanges = true,
        isSaving = false,
        saveError = "Handle must be 2–30 characters: letters, numbers, or underscores only",
        onNameChanged = {},
        onHandleChanged = {},
        onSaveProfile = {},
    )
}
