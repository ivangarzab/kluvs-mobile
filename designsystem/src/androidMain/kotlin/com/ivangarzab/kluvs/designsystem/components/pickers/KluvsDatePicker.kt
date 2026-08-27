@file:OptIn(ExperimentalMaterial3Api::class)

package com.ivangarzab.kluvs.designsystem.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Centered-[Dialog] wrapper around M3's [DatePicker] — same eyebrow-header/divider/footer shell
 * as [KluvsTimePicker] and [com.ivangarzab.kluvs.designsystem.components.modals.ConfirmationDialog],
 * instead of the stock [androidx.compose.material3.DatePickerDialog] the three call sites
 * (Create/Edit Session, Discussion) used to hand-roll separately.
 *
 * Unlike [KluvsTimePicker], no typography override is needed here — DatePicker's roles
 * (headlineLarge/titleLarge/labelLarge/bodyLarge, confirmed by decompiling the M3 jar) are all
 * close enough to our brand sizes not to clip. The stock `title` ("Select date") is suppressed
 * since it would duplicate our own eyebrow header; the `headline` (big date preview + keyboard-
 * entry toggle) is kept since it's functional, not just decorative.
 *
 * Two fixes M3's own `DatePickerDialog` bakes in that a bare [Dialog] doesn't, confirmed by
 * decompiling the M3 jar:
 * - `DialogProperties(usePlatformDefaultWidth = false)` — the default (`true`) caps the window at
 *   the platform's default dialog width, which is narrower than the calendar grid actually needs,
 *   clipping the rightmost day-of-week column. M3's own `DatePickerDialog` explicitly disables it.
 * - `containerColor` — `DatePicker` defaults to `colorScheme.surface` (our `warmDarkCard`), while
 *   the wrapping [Column] below uses `KluvsTheme.colors.bar` (`warmDarkBar`) like every other
 *   sheet/dialog — mismatched, producing a visible two-tone box. Overridden to match.
 */
@Composable
fun KluvsDatePicker(
    initialSelectedDateMillis: Long? = null,
    onConfirm: (selectedDateMillis: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        KluvsDatePickerContent(
            state = state,
            onConfirm = { state.selectedDateMillis?.let(onConfirm) },
            onDismiss = onDismiss,
        )
    }
}

/**
 * The dialog's actual visual content — split out for the same reason as
 * [KluvsTimePicker]'s: [Dialog] content doesn't render in Compose's static preview renderer, so
 * this is called directly by the `@Preview` below instead of duplicating the layout by hand.
 */
@Composable
private fun KluvsDatePickerContent(
    state: DatePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val containerColor = KluvsTheme.colors.bar
    val dividerColor = KluvsTheme.colors.divider

    Column(
        modifier = Modifier.background(containerColor, RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = "SELECT DATE",
            style = KluvsTheme.typography.eyebrow,
            color = KluvsTheme.colors.accent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 20.dp),
        )
        HorizontalDivider(color = dividerColor)

        DatePicker(
            state = state,
            title = null,
            colors = DatePickerDefaults.colors(containerColor = containerColor),
        )

        HorizontalDivider(color = dividerColor)
        BottomSheetFooter(
            actionLabel = "OK",
            onAction = onConfirm,
            onCancel = onDismiss,
            actionEnabled = state.selectedDateMillis != null,
        )
    }
}

/**
 * Same Compose Preview limitation as [KluvsTimePicker] (Dialog content doesn't render in a
 * static preview) — use Android Studio's Interactive Mode or a real device to preview the real
 * [KluvsDatePicker] itself.
 */
@PreviewLightDark
@Composable
private fun Preview_KluvsDatePickerContent() = KluvsTheme {
    KluvsDatePickerContent(
        state = rememberDatePickerState(),
        onConfirm = {},
        onDismiss = {},
    )
}
