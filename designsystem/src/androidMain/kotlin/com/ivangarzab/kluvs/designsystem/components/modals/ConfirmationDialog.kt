package com.ivangarzab.kluvs.designsystem.components.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme

/**
 * Centered confirm/cancel dialog — design-system "Modal", centered-dialog form (see
 * design-system/docs/modal.md): eyebrow header, divider, body, divider, footer — the same
 * three-zone shell [BottomSheet] uses. Built on a raw [Dialog], not M3's `AlertDialog` —
 * AlertDialog's fixed title/text/button slots can't produce this structure (no divider, no
 * full-bleed header) no matter what's passed to it. A previous version of this component used
 * `AlertDialog` and rendered as a plain default dialog in the running app, while a hand-copied
 * `@Preview` reconstruction (kept only because Dialog/Popup content doesn't render in Compose's
 * static preview renderer) showed the intended layout and silently drifted from what actually
 * shipped. [ConfirmationDialogContent] below is now the single shared source for both, so that
 * kind of drift can't happen again.
 *
 * Reserved for hard, confirm-only confirmations (Delete Club, Sign Out, Remove Member) —
 * anything with fields to fill in belongs in [BottomSheet] instead.
 *
 * @param isDestructive true for irreversible actions (delete/remove) — tints the title and
 * confirm label danger-red instead of copper. Sign-out-style confirmations (reversible, just
 * disruptive) should pass false.
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = "Confirm",
    dismissLabel: String = "Cancel",
    isDestructive: Boolean = false,
) {
    Dialog(onDismissRequest = onDismiss) {
        ConfirmationDialogContent(
            title = title,
            message = message,
            confirmLabel = confirmLabel,
            dismissLabel = dismissLabel,
            isDestructive = isDestructive,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
        )
    }
}

/**
 * The dialog's actual visual content — shared by [ConfirmationDialog] (wrapped in a real
 * [Dialog] window at runtime) and the `@Preview` functions below (called directly, since
 * [Dialog]'s content doesn't render in Compose's static preview renderer — a long-standing
 * limitation shared by any Popup/Dialog-based composable. Use Android Studio's Interactive
 * Mode, or a real device/emulator, to preview [ConfirmationDialog] itself).
 */
@Composable
private fun ConfirmationDialogContent(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    isDestructive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val accentColor = if (isDestructive) KluvsTheme.colors.danger else KluvsTheme.colors.accent
    val containerColor = KluvsTheme.colors.bar
    val dividerColor = KluvsTheme.colors.divider

    Column(
        modifier = Modifier.background(containerColor, RoundedCornerShape(16.dp)),
    ) {
        // Header — eyebrow label, border-bottom.
        Text(
            text = title.uppercase(),
            style = KluvsTheme.typography.eyebrow,
            color = accentColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 20.dp),
        )
        HorizontalDivider(color = dividerColor)

        // Body.
        Text(
            text = message,
            style = KluvsTheme.typography.body.medium,
            color = KluvsTheme.colors.contentMuted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        )

        // Footer — Cancel leading (ghost text), action trailing, border-top.
        HorizontalDivider(color = dividerColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = KluvsTheme.colors.contentMuted),
            ) {
                Text(dismissLabel)
            }
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = accentColor),
            ) {
                Text(confirmLabel)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview_ConfirmationDialog_Destructive() = KluvsTheme {
    ConfirmationDialogContent(
        title = "Delete Club",
        message = "Are you sure you want to delete this club? This action cannot be undone.",
        confirmLabel = "Delete",
        dismissLabel = "Cancel",
        isDestructive = true,
        onConfirm = {},
        onDismiss = {},
    )
}

@PreviewLightDark
@Composable
private fun Preview_ConfirmationDialog_Default() = KluvsTheme {
    ConfirmationDialogContent(
        title = "Sign Out",
        message = "Are you sure you want to sign out?",
        confirmLabel = "Sign Out",
        dismissLabel = "Cancel",
        isDestructive = false,
        onConfirm = {},
        onDismiss = {},
    )
}
