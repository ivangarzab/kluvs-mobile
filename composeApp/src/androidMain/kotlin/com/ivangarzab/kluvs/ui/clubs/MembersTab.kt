package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.clubs.presentation.MemberListItemInfo
import com.ivangarzab.kluvs.clubs.presentation.SessionParticipantInfo
import com.ivangarzab.kluvs.model.Role
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.feature
import com.ivangarzab.kluvs.designsystem.components.avatars.Avatar
import com.ivangarzab.kluvs.designsystem.components.buttons.OutlinedButton
import com.ivangarzab.kluvs.designsystem.components.buttons.PrimaryButton
import com.ivangarzab.kluvs.designsystem.components.icons.Icon
import com.ivangarzab.kluvs.designsystem.components.icons.IconType
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenu
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenuItem
import com.ivangarzab.kluvs.designsystem.components.NoTabData
import com.ivangarzab.kluvs.ui.components.RoleEyebrow

@Composable
fun MembersTab(
    modifier: Modifier = Modifier,
    members: List<MemberListItemInfo>,
    participants: List<SessionParticipantInfo> = emptyList(),
    currentUserId: String? = null,
    userRole: Role? = null,
    onChangeRole: (memberId: String) -> Unit = {},
    onRemoveMember: (memberId: String) -> Unit = {},
    onInviteMember: () -> Unit = {},
) {
    // Session participation lookup for the reading/skipping indicator
    val readingByMemberId = participants.associate { it.memberId to it.isReading }
    if (members.isEmpty()) {
        NoTabData(
            modifier = modifier,
            text = R.string.no_members_in_club
        )
        return
    }

    val isAdminOrAbove = userRole == Role.OWNER || userRole == Role.ADMIN
    val isOwner = userRole == Role.OWNER

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.x_members, members.size),
                color = KluvsTheme.colors.contentMuted,
                style = KluvsTheme.typography.title.small.feature()
            )
            if (isAdminOrAbove) {
                OutlinedButton(
                    text = stringResource(R.string.invite),
                    onClick = onInviteMember
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            itemsIndexed(members) { index, member ->
                //TODO: Consider creating an ext function: Member.isMe(Member or Member.id)
                val isSelf = member.userId != null && member.userId == currentUserId
                MemberListItem(
                    memberId = member.memberId,
                    name = member.name,
                    handle = member.handle,
                    avatarUrl = member.avatarUrl,
                    role = member.role,
                    isSelf = isSelf,
                    isReading = readingByMemberId[member.memberId],
                    showAdminActions = isAdminOrAbove && (!isSelf || isOwner),
                    showRemove = isOwner && !isSelf && member.role != Role.OWNER,
                    onChangeRole = { onChangeRole(member.memberId) },
                    onRemove = { onRemoveMember(member.memberId) }
                )
                if (index < members.size - 1) {
                    MemberDivider()
                }
            }
        }

        if (members.size <= 1 && isAdminOrAbove) {
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.invite_others_cta),
                    style = KluvsTheme.typography.headline.small.feature(),
                    color = KluvsTheme.colors.contentMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                PrimaryButton(
                    text = stringResource(R.string.invite_members),
                    onClick = onInviteMember,
                )
            }
        }
    }
}

@Composable
private fun MemberDivider() {
    HorizontalDivider(color = KluvsTheme.colors.cardAlt)
}

@PreviewLightDark
@Composable
fun Preview_MembersTab() = KluvsTheme {
    MembersTab(
        modifier = Modifier
            .background(color = KluvsTheme.colors.card)
            .fillMaxSize(),
        members = listOf(
            MemberListItemInfo("0", "Iván Garza Bermea", "ivangarzab", "", role = Role.OWNER, userId = "u0"),
            MemberListItemInfo("1", "Monica Michelle Morales", "monica", "", role = Role.ADMIN, userId = "u1"),
            MemberListItemInfo("2", "Marco \"Chitho\" Rivera", "chitho23", "", role = Role.MEMBER, userId = "u2"),
            MemberListItemInfo("3", "Anacleto \"Keto\" Longoria", "keto92", "", role = Role.MEMBER, userId = "u3"),
            MemberListItemInfo("4", "Joel Oscar Julian Salinas", "josalinas", "", role = Role.MEMBER, userId = "u4"),
            MemberListItemInfo("5", "Ginseng Joaquin Guzman", "gino1", "", role = Role.MEMBER, userId = "u5"),
        ),
        currentUserId = "u0",
        userRole = Role.OWNER
    )
}
