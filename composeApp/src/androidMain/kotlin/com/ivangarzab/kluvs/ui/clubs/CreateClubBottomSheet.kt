package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Bottom sheet for creating a new club — matches web's `AddClubModal` scope on mobile
 * (name only; Discord server/channel selection needs the `discord-channels` endpoint,
 * which isn't built yet). Same deferred gap already noted on Edit Club.
 *
 * Last of the 9 content sheets — every real ModalBottomSheet-presented content view in the app
 * now goes through the shared BottomSheet shell.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClubBottomSheet(
    onCreate: (name: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val trimmedName = name.trim()

    BottomSheet(
        header = "New Club",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Create",
                onAction = { onCreate(trimmedName) },
                onCancel = onDismiss,
                actionEnabled = trimmedName.isNotEmpty(),
            )
        },
    ) {
        InputField(label = "Club Name", value = name, onValueChange = { name = it })
    }
}

@PreviewLightDark
@Composable
fun Preview_CreateClubBottomSheet() = KluvsTheme {
    CreateClubBottomSheet(
        onCreate = {},
        onDismiss = {}
    )
}
