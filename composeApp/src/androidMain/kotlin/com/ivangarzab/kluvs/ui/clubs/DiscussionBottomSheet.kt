package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.fields.PickerField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.components.pickers.KluvsTimePicker
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import kotlinx.datetime.LocalDateTime

/**
 * Bottom sheet for creating or editing a discussion. Used for both create (empty fields) and edit
 * (pre-filled title/location) modes.
 *
 * Checked kluvs-frontend's DiscussionModal.tsx as source of truth: Location is optional there
 * (only Title/Date/Time are required) — this sheet previously required Location too via its
 * canSave check, a real bug, now fixed to match web. Also adds the "READING SESSION" read-only
 * info box web shows ([bookTitle], the active session's book title), which this sheet never had.
 *
 * DatePickerDialog stays native — system UI, not design-system content. TimePicker is shelled
 * via [KluvsTimePicker] instead, to match every other sheet's header/footer chrome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionBottomSheet(
    isEditing: Boolean = false,
    initialTitle: String = "",
    initialLocation: String = "",
    initialDate: LocalDateTime? = null,
    bookTitle: String? = null,
    onSave: (title: String, location: String, date: LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialDateMillis = remember { initialDate?.let { localDateTimeToDateMillis(it) } }
    var title by remember { mutableStateOf(initialTitle) }
    var location by remember { mutableStateOf(initialLocation) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(initialDateMillis) }
    var selectedHour by remember { mutableStateOf(initialDate?.hour ?: 19) }
    var selectedMinute by remember { mutableStateOf(initialDate?.minute ?: 0) }

    val dateDisplayText = selectedDateMillis?.let { formatDateMillis(it) } ?: ""
    val timeDisplayText = selectedDateMillis?.let {
        "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
    } ?: ""

    val canSave = title.isNotBlank() && selectedDateMillis != null
    val hasChanges = title.trim() != initialTitle ||
        location.trim() != initialLocation ||
        selectedDateMillis != initialDateMillis ||
        selectedHour != (initialDate?.hour ?: 19) ||
        selectedMinute != (initialDate?.minute ?: 0)

    BottomSheet(
        header = if (isEditing) "Edit Discussion" else "Add Discussion",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = if (isEditing) "Update Discussion" else "Add Discussion",
                onAction = {
                    val millis = selectedDateMillis ?: return@BottomSheetFooter
                    val dateTime = buildLocalDateTime(millis, selectedHour, selectedMinute)
                    onSave(title.trim(), location.trim(), dateTime)
                },
                onCancel = onDismiss,
                actionEnabled = canSave && hasChanges,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (bookTitle != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KluvsTheme.colors.cardAlt, RoundedCornerShape(8.dp))
                        .border(1.dp, KluvsTheme.colors.divider, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = "READING SESSION",
                        style = KluvsTheme.typography.eyebrow,
                        color = KluvsTheme.colors.contentMuted,
                    )
                    Text(
                        text = bookTitle,
                        style = KluvsTheme.typography.body.medium,
                        color = KluvsTheme.colors.content,
                    )
                }
            }

            InputField(label = "Title", value = title, onValueChange = { title = it })
            // InputField has no leading-icon slot (unlike the raw OutlinedTextField this
            // replaces), so the location pin icon this field used to show is dropped — a minor
            // decorative loss, not a functional one.
            InputField(label = "Location (optional)", value = location, onValueChange = { location = it })
            PickerField(label = "Date", value = dateDisplayText, onClick = { showDatePicker = true })
            if (selectedDateMillis != null) {
                PickerField(label = "Time", value = timeDisplayText, onClick = { showTimePicker = true })
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        KluvsTimePicker(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            onConfirm = { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_DiscussionBottomSheet() = KluvsTheme {
    DiscussionBottomSheet(
        initialTitle = "Discussion title",
        initialLocation = "Discussion location",
        onSave = { _, _, _ -> },
        onDismiss = {}
    )
}
