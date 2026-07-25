package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
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
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheet
import com.ivangarzab.kluvs.designsystem.components.modals.BottomSheetFooter
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.model.Role

private val assignableRoles = listOf(Role.ADMIN, Role.MEMBER)

/**
 * Bottom sheet for changing a member's role within a club.
 *
 * Only ADMIN and MEMBER are assignable — OWNER is reserved for the club creator. Scoped to role
 * selection only, matching what this sheet already did — web's `MemberModal` folds in a
 * per-session "reading" toggle and a combined Add/Edit Member flow, but neither is part of this
 * sheet on mobile today (adding a member happens via invite link, not a name field here; the
 * reading toggle needs a new session-members PUT call not currently wired to this flow) — both
 * left out rather than silently expanded, same reasoning as Edit Club's deferred Discord/Founded
 * Date fields.
 *
 * Keeps Android's own RadioButton selection widget rather than iOS's checkmark-on-row — a
 * deliberate platform-idiom difference, not a drift; RadioButton is the standard Android affordance
 * for this exact "pick one of a short list" shape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRoleBottomSheet(
    memberName: String,
    currentRole: Role,
    onSave: (newRole: Role) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialSelectedRole = remember { currentRole.takeIf { it in assignableRoles } ?: Role.MEMBER }
    var selectedRole by remember { mutableStateOf(initialSelectedRole) }

    BottomSheet(
        header = "Change Role",
        onDismiss = onDismiss,
        footer = {
            BottomSheetFooter(
                actionLabel = "Save",
                onAction = { onSave(selectedRole) },
                onCancel = onDismiss,
                actionEnabled = selectedRole != initialSelectedRole,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = memberName.uppercase(),
                style = KluvsTheme.typography.eyebrow,
                color = KluvsTheme.colors.contentMuted,
            )

            assignableRoles.forEach { role ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedRole == role,
                        onClick = { selectedRole = role }
                    )
                    Column {
                        Text(
                            text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = KluvsTheme.typography.body.medium,
                            color = KluvsTheme.colors.content,
                        )
                        Text(
                            text = when (role) {
                                Role.ADMIN -> "Can create and manage sessions and discussions"
                                else -> "Regular club member"
                            },
                            style = KluvsTheme.typography.caption,
                            color = KluvsTheme.colors.contentMuted,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun Preview_ChangeRoleBottomSheet() = KluvsTheme {
    ChangeRoleBottomSheet(
        memberName = "Bob Smith",
        currentRole = Role.MEMBER,
        onSave = {},
        onDismiss = {}
    )
}
