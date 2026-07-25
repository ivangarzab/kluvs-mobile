package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.clubs.presentation.DiscussionNoteInfo
import com.ivangarzab.kluvs.designsystem.components.buttons.PrimaryButton
import com.ivangarzab.kluvs.designsystem.components.buttons.TextButton
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.ConfirmationDialog
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

private const val NOTE_MAX_LENGTH = 4000

/**
 * Bottom sheet for viewing, creating, editing, or deleting the signed-in
 * member's note on a discussion.
 *
 * A null [note] means it hasn't finished loading yet. A non-null [note] with
 * [DiscussionNoteInfo.noteId] null means no note exists yet, so the sheet
 * opens straight into an editable/create state.
 *
 * Mobile-only feature, no web equivalent to check. Renders its own Edit/Delete or Cancel/Save
 * button row as content rather than using BottomSheetFooter's slot — same reasoning as iOS's
 * conversion: this component has two modes with different action pairs depending on internal
 * state, not the fixed Cancel/Action shape BottomSheetFooter assumes. Kept the raw multi-line
 * OutlinedTextField for the note editor rather than forcing it into InputField — InputField
 * requires a label, which would be redundant given the sheet header already says "Note".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionNoteSheet(
    note: DiscussionNoteInfo?,
    onSave: (content: String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var isEditing by remember(note?.noteId) { mutableStateOf(note?.noteId == null) }
    var content by remember(note?.content) { mutableStateOf(note?.content ?: "") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    BottomSheet(
        header = "Note",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when {
                note == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                isEditing -> {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { if (it.length <= NOTE_MAX_LENGTH) content = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5,
                        maxLines = 10
                    )

                    val errorMessage = note.error
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            style = KluvsTheme.typography.caption,
                            color = KluvsTheme.colors.danger,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (note.noteId != null) {
                            TextButton(text = "Cancel", onClick = {
                                content = note.content
                                isEditing = false
                            })
                            Spacer(Modifier.width(8.dp))
                        }
                    }

                    val canSave = content.isNotBlank() && content.trim() != note.content.trim()
                    PrimaryButton(
                        text = if (note.isSaving) "Saving…" else "Save",
                        onClick = { onSave(content.trim()) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canSave && !note.isSaving,
                    )
                }

                else -> {
                    Text(
                        text = note.content,
                        style = KluvsTheme.typography.body.medium,
                        color = KluvsTheme.colors.content,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(text = "Edit", onClick = { isEditing = true })
                        TextButton(text = "Delete", onClick = { showDeleteConfirmation = true })
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Delete Note",
            message = "Are you sure you want to delete this note?",
            confirmLabel = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteConfirmation = false
                onDelete()
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_DiscussionNoteSheet() = KluvsTheme {
    DiscussionNoteSheet(
        note = DiscussionNoteInfo(noteId = "n1", content = "Bring snacks next time and discuss chapter 5."),
        onSave = {},
        onDelete = {},
        onDismiss = {}
    )
}
