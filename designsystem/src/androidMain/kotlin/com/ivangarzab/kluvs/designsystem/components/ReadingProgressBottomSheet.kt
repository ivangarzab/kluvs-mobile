package com.ivangarzab.kluvs.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.designsystem.components.controls.ToggleControl
import com.ivangarzab.kluvs.designsystem.components.fields.InputField
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import kotlin.math.roundToInt

/** Hollow tracking-mode option — decoupled from the app's `ProgressType` domain enum. */
enum class ProgressTrackingMode { PAGE, PERCENT }

/**
 * Bottom sheet for tracking/updating the signed-in member's reading progress.
 *
 * Shared component: used by the Clubs screen (session progress) and the Me screen (shelf rows).
 * Checked kluvs-frontend's ReadingProgressModal.tsx as source of truth — already matched closely
 * functionally (Page/Percent toggle, auto "mark as finished" at the end of the book, manual
 * finished switch). This pass adds the elevated "BOOK" info box web shows, which this sheet
 * previously rendered as a plain italic Text line instead, and swaps raw OutlinedTextField for
 * InputField.
 *
 * Hollow — takes [ProgressTrackingMode] instead of the app's `ProgressType`; [onSave] reports back
 * in the same hollow currency, so callers translate at the boundary.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingProgressBottomSheet(
    bookTitle: String,
    pageCount: Int?,
    initialType: ProgressTrackingMode = ProgressTrackingMode.PAGE,
    initialCurrentPage: Int? = null,
    initialPercentComplete: Float? = null,
    initialMarkFinished: Boolean = false,
    onSave: (type: ProgressTrackingMode, currentPage: Int?, percentComplete: Float?, markFinished: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var progressType by rememberSaveable { mutableStateOf(initialType) }
    var currentPageText by rememberSaveable {
        mutableStateOf(initialCurrentPage?.toString() ?: "")
    }
    var percentText by rememberSaveable {
        mutableStateOf(initialPercentComplete?.let { formatPercentInput(it) } ?: "")
    }
    var markFinished by rememberSaveable { mutableStateOf(initialMarkFinished) }
    // Tracks the last value that auto-toggled the switch, so a manual override
    // sticks until the value changes again (same semantics as the web modal)
    var lastAutoTriggerValue by remember { mutableStateOf<String?>(null) }

    fun autoToggleFinished(newValue: String) {
        if (newValue == lastAutoTriggerValue) return
        val atEnd = when {
            progressType == ProgressTrackingMode.PAGE && pageCount != null && pageCount > 0 ->
                (newValue.toIntOrNull() ?: 0) >= pageCount
            progressType == ProgressTrackingMode.PERCENT ->
                (newValue.toFloatOrNull() ?: 0f) >= 100f
            else -> return
        }
        if (atEnd != markFinished) {
            markFinished = atEnd
            lastAutoTriggerValue = newValue
        }
    }

    val previewPercent = if (progressType == ProgressTrackingMode.PAGE && pageCount != null && pageCount > 0) {
        val page = currentPageText.toIntOrNull()
        page?.let { minOf(100, (it * 100f / pageCount).roundToInt()) }
    } else null

    val canSave = when (progressType) {
        ProgressTrackingMode.PAGE -> currentPageText.toIntOrNull() != null
        ProgressTrackingMode.PERCENT -> percentText.toFloatOrNull() != null
    }

    BottomSheet(
        header = if (initialCurrentPage != null || initialPercentComplete != null) "Update Progress" else "Track Progress",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Save Progress",
                onAction = {
                    val page = if (progressType == ProgressTrackingMode.PAGE) currentPageText.toIntOrNull() else null
                    val percent = if (progressType == ProgressTrackingMode.PERCENT) {
                        percentText.toFloatOrNull()?.coerceIn(0f, 100f)
                    } else null
                    onSave(progressType, page, percent, markFinished)
                },
                onCancel = onDismiss,
                actionEnabled = canSave,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KluvsTheme.colors.cardAlt, RoundedCornerShape(8.dp))
                    .border(1.dp, KluvsTheme.colors.divider, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "BOOK",
                    style = KluvsTheme.typography.eyebrow,
                    color = KluvsTheme.colors.contentMuted,
                )
                Text(
                    text = bookTitle,
                    style = KluvsTheme.typography.body.medium,
                    color = KluvsTheme.colors.content,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "TRACK BY",
                    style = KluvsTheme.typography.eyebrow,
                    color = KluvsTheme.colors.contentMuted,
                )
                ToggleControl(
                    options = listOf(ProgressTrackingMode.PAGE, ProgressTrackingMode.PERCENT),
                    selected = progressType,
                    onSelect = { progressType = it },
                    label = { if (it == ProgressTrackingMode.PAGE) "Page" else "Percent" },
                    modifier = Modifier.fillMaxWidth()
                )

                if (progressType == ProgressTrackingMode.PAGE) {
                    InputField(
                        label = "Current Page" + (pageCount?.let { " (of $it)" } ?: ""),
                        value = currentPageText,
                        onValueChange = { value ->
                            currentPageText = value.filter { it.isDigit() }
                            autoToggleFinished(currentPageText)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        helperText = previewPercent?.let { "That's about $it% complete." },
                    )
                } else {
                    InputField(
                        label = "Percent Complete",
                        value = percentText,
                        onValueChange = { value ->
                            percentText = value.filter { it.isDigit() || it == '.' }
                            autoToggleFinished(percentText)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        suffix = "%",
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mark as finished",
                    style = KluvsTheme.typography.body.medium,
                    color = KluvsTheme.colors.content,
                )
                Switch(
                    checked = markFinished,
                    onCheckedChange = { markFinished = it }
                )
            }
        }
    }
}

private fun formatPercentInput(value: Float): String =
    if (value == value.toInt().toFloat()) value.toInt().toString() else value.toString()

@PreviewLightDark
@Composable
private fun Preview_ReadingProgressBottomSheet() = KluvsTheme {
    ReadingProgressBottomSheet(
        bookTitle = "1984",
        pageCount = 328,
        initialCurrentPage = 42,
        onSave = { _, _, _, _ -> },
        onDismiss = {}
    )
}
