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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.theme.KluvsTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Bottom sheet for creating or editing a discussion.
 *
 * Used for both create (empty fields) and edit (pre-filled title/location) modes.
 * The date is always entered fresh via a date picker + time text field.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DiscussionBottomSheet(
    initialTitle: String = "",
    initialLocation: String = "",
    onSave: (title: String, location: String, date: LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var location by remember { mutableStateOf(initialLocation) }
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
                text = "Discussion",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null
                    )
                }
            )

            // Date picker field — tap anywhere on the field to open picker
            val dateDisplayText = selectedDateMillis?.let { millis ->
                val dt = Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.UTC)
                "${dt.year}-${dt.monthNumber.toString().padStart(2, '0')}-${dt.dayOfMonth.toString().padStart(2, '0')}"
            } ?: ""

            Box {
                OutlinedTextField(
                    value = dateDisplayText,
                    onValueChange = {},
                    label = { Text("Date") },
                    placeholder = { Text("Select date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true
                )
                // Transparent overlay to capture the click and open the date picker
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .let {
                            it.then(
                                Modifier.padding(0.dp)
                            )
                        }
                ) {
                    TextButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.matchParentSize()
                    ) { }
                }
            }

            OutlinedTextField(
                value = timeText,
                onValueChange = { timeText = it },
                label = { Text("Time (HH:MM)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            val canSave = title.isNotBlank() && location.isNotBlank() && selectedDateMillis != null
            Button(
                onClick = {
                    val dateTime = buildDiscussionDateTime(selectedDateMillis!!, timeText)
                    onSave(title.trim(), location.trim(), dateTime)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave
            ) {
                Text("Save")
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
private fun buildDiscussionDateTime(epochMillis: Long, timeText: String): LocalDateTime {
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
fun Preview_DiscussionBottomSheet() = KluvsTheme {
    DiscussionBottomSheet(
        initialTitle = "Discussion title",
        initialLocation = "Discussion location",
        onSave = { _, _, _ -> },
        onDismiss = {}
    )
}
