package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.designsystem.components.avatars.Avatar
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenu
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenuItem
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.model.Role
import com.ivangarzab.kluvs.ui.components.RoleEyebrow

@Composable
fun MemberListItem(
    modifier: Modifier = Modifier,
    memberId: String,
    name: String,
    handle: String,
    avatarUrl: String? = null,
    role: Role,
    isSelf: Boolean = false,
    isReading: Boolean? = null,
    showAdminActions: Boolean = false,
    showRemove: Boolean = false,
    showTransferOwnership: Boolean = false,
    onChangeRole: () -> Unit = {},
    onRemove: () -> Unit = {},
    onTransferOwnership: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            name = name,
            memberId = memberId,
            avatarUrl = avatarUrl,
            size = 40.dp,
            isOwn = isSelf,
            contentDescription = stringResource(R.string.avatar_of_x, name)
        )

        // Left: identity column, now three lines (name, handle, books read) so it carries
        // as much weight as the right rail's two tiers instead of looking sparse next to it.
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = KluvsTheme.colors.content,
                    style = KluvsTheme.typography.body.large
                )
                if (isSelf) {
                    Text(
                        text = stringResource(R.string.you).uppercase(),
                        color = KluvsTheme.colors.accent,
                        style = KluvsTheme.typography.eyebrow
                    )
                }
            }
            Text(
                text = "@$handle",
                color = KluvsTheme.colors.contentMuted,
                style = KluvsTheme.typography.body.medium
            )
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                isReading?.let { reading ->
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp),
                        type = IconType.Reading,
                        contentDescription = if (reading) {
                            stringResource(R.string.member_reading)
                        } else {
                            stringResource(R.string.member_skipping)
                        },
                        tint = if (reading) KluvsTheme.colors.accent else KluvsTheme.colors.contentMuted
                    )
                }

                if (showAdminActions || showRemove || showTransferOwnership) {
                    ActionMenu(
                        items = buildList {
                            if (showAdminActions) add(ActionMenuItem(label = "Change Role", onClick = onChangeRole))
                            if (showTransferOwnership) add(ActionMenuItem(label = "Make Owner", onClick = onTransferOwnership))
                            if (showRemove) add(ActionMenuItem(label = "Remove", onClick = onRemove, isDestructive = true))
                        },
                        contentDescription = "Member options"
                    )
                }
            }

            if (role != Role.MEMBER) {
                RoleEyebrow(role = role)
            } else {
                Spacer(modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun Preview_MemberListItem() = KluvsTheme {
    Column(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 4.dp)
    ) {
        MemberListItem(
            memberId = "0",
            name = "Ivan Garza",
            handle = "ivangarzab",
            role = Role.MEMBER,
            isSelf = true,
        )
    }
}

@Preview
@Composable
fun Preview_MemberListItem_Reading() = KluvsTheme {
    Column(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 4.dp)
    ) {
        MemberListItem(
            memberId = "0",
            name = "Ivan Garza",
            handle = "ivangarzab",
            role = Role.MEMBER,
            isSelf = true,
            isReading = true,
        )
    }
}

@Preview
@Composable
fun Preview_MemberListItem_Member_Actions() = KluvsTheme {
    Column(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 4.dp)
    ) {
        MemberListItem(
            memberId = "0",
            name = "Ivan Garza",
            handle = "ivangarzab",
            role = Role.MEMBER,
            showAdminActions = true,
            isSelf = true,
            isReading = true,
        )
    }
}

@Preview
@Composable
fun Preview_MemberListItem_Owner() = KluvsTheme {
    Column(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 4.dp)
    ) {
        MemberListItem(
            memberId = "0",
            name = "Ivan Garza",
            handle = "ivangarzab",
            role = Role.OWNER,
            showAdminActions = true,
            isSelf = true,
        )
    }
}

@Preview
@Composable
fun Preview_MemberListItem_Full() = KluvsTheme {
    Column(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 4.dp)
    ) {
        MemberListItem(
            memberId = "0",
            name = "Ivan Garza",
            handle = "ivangarzab",
            role = Role.OWNER,
            showAdminActions = true,
            isSelf = true,
            isReading = true,
        )
    }
}