package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.theme.KluvsTheme
import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Bottom sheet for creating a new reading session.
 *
 * Collects book title, author, and an optional due date.
 * Full book search (via SearchBooksUseCase) is a planned follow-up; for now the user
 * enters book details manually.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CreateSessionBottomSheet(
    onSave: (book: Book, dueDate: LocalDateTime?) -> Unit,
    onDismiss: () -> Unit,
) {
    var bookTitle by remember { mutableStateOf("") }
    var bookAuthor by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var timeText by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Create Session",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = bookTitle,
                onValueChange = { bookTitle = it },
                label = { Text("Book Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = bookAuthor,
                onValueChange = { bookAuthor = it },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            val dateDisplayText = selectedDateMillis?.let { millis ->
                val dt = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC)
                "${dt.year}-${dt.monthNumber.toString().padStart(2, '0')}-${dt.dayOfMonth.toString().padStart(2, '0')}"
            } ?: ""

            Box {
                OutlinedTextField(
                    value = dateDisplayText,
                    onValueChange = {},
                    label = { Text("Due Date (optional)") },
                    placeholder = { Text("Select date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true
                )
                TextButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.matchParentSize()
                ) { }
            }

            if (selectedDateMillis != null) {
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = { Text("Time (HH:MM, optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Button(
                onClick = {
                    val book = Book(
                        id = "",
                        title = bookTitle.trim(),
                        author = bookAuthor.trim(),
                        isbn = null
                    )
                    val dueDate = selectedDateMillis?.let { millis ->
                        buildSessionDateTime(millis, timeText)
                    }
                    onSave(book, dueDate)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = bookTitle.isNotBlank() && bookAuthor.isNotBlank()
            ) {
                Text("Create")
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
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun buildSessionDateTime(epochMillis: Long, timeText: String): LocalDateTime {
    val date = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.UTC)
    val parts = timeText.split(":")
    val hour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
    return LocalDateTime(
        year = date.year,
        month = date.month,
        day = date.day,
        hour = hour.coerceIn(0, 23),
        minute = minute.coerceIn(0, 59)
    )
}

@PreviewLightDark
@Composable
fun Preview_CreateSessionBottomSheet() = KluvsTheme {
    CreateSessionBottomSheet(
        onSave = { _, _ -> },
        onDismiss = {}
    )
}
