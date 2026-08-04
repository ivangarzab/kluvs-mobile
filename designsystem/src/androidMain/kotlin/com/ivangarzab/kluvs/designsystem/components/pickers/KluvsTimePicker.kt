@file:OptIn(ExperimentalMaterial3Api::class)

package com.ivangarzab.kluvs.designsystem.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.ibmPlexSans

/**
 * Centered-[Dialog] wrapper around M3's [TimePicker] — same eyebrow-header/divider/footer shell
 * as [com.ivangarzab.kluvs.designsystem.components.modals.ConfirmationDialog] and [com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet],
 * instead of the stock [androidx.compose.material3.AlertDialog] the three call sites (Create/Edit
 * Session, Discussion) used to hand-roll separately. A quick single-value pick reads more
 * naturally as a centered dialog than a full-height bottom sheet.
 *
 * Bakes in two fixes that are otherwise easy to lose track of across call sites:
 * - `displayLarge` is what `TimePickerTokens.TimeSelectorLabelTextFont` resolves to (confirmed by
 *   decompiling the M3 jar) — our brand `displayLarge` is a 96sp/104sp hero wordmark size, way past
 *   what the fixed digit slot is built for, so it clips. Overridden here to M3's stock 57sp/64sp,
 *   in the sans register (lining figures — the serif's old-style figures dip/rise off-baseline).
 * - AM/PM segment uses copper (`KluvsTheme.colors.accent`) instead of M3's default tertiary teal.
 */
@Composable
fun KluvsTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    is24Hour: Boolean = false,
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )

    Dialog(onDismissRequest = onDismiss) {
        KluvsTimePickerContent(
            state = state,
            onConfirm = { onConfirm(state.hour, state.minute) },
            onDismiss = onDismiss,
        )
    }
}

/**
 * The dialog's actual visual content — split out, same reasoning as
 * [com.ivangarzab.kluvs.designsystem.components.modals.ConfirmationDialog]'s private content
 * function: [Dialog] content doesn't render in Compose's static preview renderer, so this is
 * called directly by the `@Preview` below instead of duplicating the layout by hand.
 */
@Composable
private fun KluvsTimePickerContent(
    state: TimePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val containerColor = KluvsTheme.colors.bar
    val dividerColor = KluvsTheme.colors.divider

    Column(
        modifier = Modifier.background(containerColor, RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = "SELECT TIME",
            style = KluvsTheme.typography.eyebrow,
            color = KluvsTheme.colors.accent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 20.dp),
        )
        HorizontalDivider(color = dividerColor)

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme,
                typography = MaterialTheme.typography.copy(
                    displayLarge = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = ibmPlexSans,
                        fontSize = 57.sp,
                        lineHeight = 64.sp,
                    )
                ),
            ) {
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        periodSelectorSelectedContainerColor = KluvsTheme.colors.accent,
                        periodSelectorSelectedContentColor = KluvsTheme.colors.onAccent,
                    ),
                )
            }
        }

        HorizontalDivider(color = dividerColor)
        BottomSheetFooter(
            actionLabel = "OK",
            onAction = onConfirm,
            onCancel = onDismiss,
        )
    }
}

/**
 * Same Compose Preview limitation as [ConfirmationDialog] (Dialog content doesn't render in a
 * static preview) — use Android Studio's Interactive Mode or a real device to preview the real
 * [KluvsTimePicker] itself.
 */
@PreviewLightDark
@Composable
private fun Preview_KluvsTimePickerContent() = KluvsTheme {
    KluvsTimePickerContent(
        state = rememberTimePickerState(initialHour = 19, initialMinute = 0, is24Hour = false),
        onConfirm = {},
        onDismiss = {},
    )
}
