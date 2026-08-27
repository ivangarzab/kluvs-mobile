package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.fields.PickerField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.components.pickers.KluvsDatePicker
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import kotlinx.datetime.LocalDateTime

/**
 * Bottom sheet for editing an existing reading session.
 *
 * Only the due date is editable here — the session's book is intentionally not, matching
 * web's `EditSessionModal`: changing a book is treated as ending the current session and
 * starting a new one (a separate "Change Book" flow), not an edit. That flow is out of scope
 * here; this sheet simply doesn't offer a book field.
 *
 * Due date is day-only — no time-of-day picker. A picked date is modeled as a deadline of
 * end-of-that-day (23:59) when built into a [LocalDateTime] for the shared session model.
 *
 * Date picker is shelled via [KluvsDatePicker], to match every other sheet's header/footer chrome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionBottomSheet(
    initialDueDate: LocalDateTime? = null,
    onSave: (dueDate: LocalDateTime?) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialDueDateMillis = remember { initialDueDate?.let { localDateTimeToDateMillis(it) } }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(initialDueDateMillis) }

    val dateDisplayText = selectedDateMillis?.let { formatDateMillis(it) } ?: ""

    val hasChanges = selectedDateMillis != initialDueDateMillis

    BottomSheet(
        header = "Edit Session",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Save",
                onAction = {
                    val dueDate = selectedDateMillis?.let { millis -> buildLocalDateTime(millis, 23, 59) }
                    onSave(dueDate)
                },
                onCancel = onDismiss,
                actionEnabled = hasChanges,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PickerField(label = "Due Date (optional)", value = dateDisplayText, onClick = { showDatePicker = true })
        }
    }

    if (showDatePicker) {
        KluvsDatePicker(
            initialSelectedDateMillis = selectedDateMillis,
            onConfirm = { millis ->
                selectedDateMillis = millis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_EditSessionBottomSheet() = KluvsTheme {
    EditSessionBottomSheet(
        onSave = { _ -> },
        onDismiss = {}
    )
}
