package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.components.modals.DangerZoneBox
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Edit Club sheet — mirrors web's `EditClubModal.tsx`: Name field + an embedded Danger Zone
 * ("Delete club…") that hands off to the existing top-level delete confirmation, matching web's
 * `onDeleteClub` callback closing this sheet and opening its delete confirmation separately.
 *
 * Web's Discord server/channel section and Founded Date field are intentionally left out —
 * Discord linking has its own dedicated ticket, and Founded Date editing needs new shared KMP
 * layer support (the view model only exposes a derived, display-only founded year today) rather
 * than being a pure UI-shell change, so both are deferred.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClubBottomSheet(
    currentName: String,
    onSave: (newName: String) -> Unit,
    onDismiss: () -> Unit,
    onDeleteClub: () -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    val trimmedName = name.trim()
    val hasChanges = trimmedName != currentName

    BottomSheet(
        header = "Edit Club",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Save",
                onAction = { onSave(trimmedName) },
                onCancel = onDismiss,
                actionEnabled = hasChanges && trimmedName.isNotEmpty(),
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            InputField(label = "Club Name", value = name, onValueChange = { name = it })
            DangerZoneBox(actionLabel = "Delete club…", onActionClick = onDeleteClub)
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_EditClubBottomSheet() = KluvsTheme {
    EditClubBottomSheet(
        currentName = "My Book Club",
        onSave = {},
        onDismiss = {},
        onDeleteClub = {},
    )
}
