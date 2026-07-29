package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.fields.PickerField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.model.Book
import kotlinx.datetime.LocalDateTime

/**
 * Bottom sheet for creating a new reading session.
 *
 * Collects book title, author, and an optional due date/time. Full book search (via
 * SearchBooksUseCase) is a planned follow-up; for now the user enters book details manually —
 * matches web's NewSessionModal gap, which uses a real BookSearchInput instead. Web's "all members
 * reading" participation toggle is the same story: no allReading param exists on the shared
 * onCreateSession call today.
 *
 * DatePickerDialog/TimePicker AlertDialogs stay native — system UI, not design-system content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionBottomSheet(
    onSave: (book: Book, dueDate: LocalDateTime?) -> Unit,
    onDismiss: () -> Unit,
) {
    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf(19) }
    var selectedMinute by remember { mutableStateOf(0) }

    val dateDisplayText = selectedDateMillis?.let { formatDateMillis(it) } ?: ""
    val timeDisplayText = selectedDateMillis?.let {
        "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
    } ?: ""

    BottomSheet(
        header = "Create Session",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Create",
                onAction = {
                    val book = Book(
                        id = "",
                        title = bookTitle.trim(),
                        author = bookAuthor.trim(),
                        isbn = null
                    )
                    val dueDate = selectedDateMillis?.let { millis ->
                        buildLocalDateTime(millis, selectedHour, selectedMinute)
                    }
                    onSave(book, dueDate)
                },
                onCancel = onDismiss,
                actionEnabled = bookTitle.isNotBlank() && bookAuthor.isNotBlank(),
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
        val datePickerState = rememberDatePickerState()
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
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_CreateSessionBottomSheet() = KluvsTheme {
    CreateSessionBottomSheet(
        onSave = { _, _ -> },
        onDismiss = {}
    )
}
