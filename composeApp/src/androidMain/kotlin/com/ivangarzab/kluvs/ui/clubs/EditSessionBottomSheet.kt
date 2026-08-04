package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.clubs.presentation.BookInfo
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.fields.PickerField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.components.pickers.KluvsTimePicker
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.model.Book
import kotlinx.datetime.LocalDateTime

/**
 * Bottom sheet for editing an existing reading session.
 *
 * Pre-fills book title and author from the current session. The due date/time are entered fresh
 * via pickers. Same manual-entry gap as CreateSessionBottomSheet vs. web's real book search —
 * documented there, not repeated here.
 *
 * DatePickerDialog stays native — system UI, not design-system content. TimePicker is shelled
 * via [KluvsTimePicker] instead, to match every other sheet's header/footer chrome.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionBottomSheet(
    currentBook: BookInfo?,
    initialDueDate: LocalDateTime? = null,
    onSave: (book: Book?, dueDate: LocalDateTime?) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialDueDateMillis = remember { initialDueDate?.let { localDateTimeToDateMillis(it) } }
    var bookTitle by remember { mutableStateOf(currentBook?.title ?: "") }
    var bookAuthor by remember { mutableStateOf(currentBook?.author ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(initialDueDateMillis) }
    var selectedHour by remember { mutableStateOf(initialDueDate?.hour ?: 19) }
    var selectedMinute by remember { mutableStateOf(initialDueDate?.minute ?: 0) }

    val dateDisplayText = selectedDateMillis?.let { formatDateMillis(it) } ?: ""
    val timeDisplayText = selectedDateMillis?.let {
        "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
    } ?: ""

    val hasChanges = bookTitle.trim() != (currentBook?.title ?: "") ||
        bookAuthor.trim() != (currentBook?.author ?: "") ||
        selectedDateMillis != initialDueDateMillis ||
        selectedHour != (initialDueDate?.hour ?: 19) ||
        selectedMinute != (initialDueDate?.minute ?: 0)

    BottomSheet(
        header = "Edit Session",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Save",
                onAction = {
                    val book = if (bookTitle.isNotBlank() && bookAuthor.isNotBlank()) {
                        Book(id = "", title = bookTitle.trim(), author = bookAuthor.trim(), isbn = null)
                    } else null
                    val dueDate = selectedDateMillis?.let { millis ->
                        buildLocalDateTime(millis, selectedHour, selectedMinute)
                    }
                    onSave(book, dueDate)
                },
                onCancel = onDismiss,
                actionEnabled = hasChanges,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InputField(label = "Book Title", value = bookTitle, onValueChange = { bookTitle = it })
            InputField(label = "Author", value = bookAuthor, onValueChange = { bookAuthor = it })
            PickerField(label = "Due Date (optional)", value = dateDisplayText, onClick = { showDatePicker = true })
            if (selectedDateMillis != null) {
                PickerField(label = "Time (optional)", value = timeDisplayText, onClick = { showTimePicker = true })
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
fun Preview_EditSessionBottomSheet() = KluvsTheme {
    EditSessionBottomSheet(
        currentBook = BookInfo(title = "1984", author = "George Orwell", year = "1948", pageCount = 169),
        onSave = { _, _ -> },
        onDismiss = {}
    )
}
