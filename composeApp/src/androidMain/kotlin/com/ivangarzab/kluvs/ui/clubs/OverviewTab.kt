package com.ivangarzab.kluvs.ui.clubs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.ivangarzab.kluvs.R
import com.ivangarzab.kluvs.clubs.presentation.ActiveSessionDetails
import com.ivangarzab.kluvs.clubs.presentation.BookInfo
import com.ivangarzab.kluvs.clubs.presentation.ClubDetails
import com.ivangarzab.kluvs.clubs.presentation.DiscussionInfo
import com.ivangarzab.kluvs.clubs.presentation.DiscussionTimelineItemInfo
import com.ivangarzab.kluvs.clubs.presentation.MemberListItemInfo
import com.ivangarzab.kluvs.presentation.progress.OwnProgressInfo
import com.ivangarzab.kluvs.clubs.presentation.SessionParticipantInfo
import com.ivangarzab.kluvs.model.ProgressType
import com.ivangarzab.kluvs.model.Role
import com.ivangarzab.kluvs.designsystem.theme.KluvsTheme
import com.ivangarzab.kluvs.designsystem.theme.feature
import com.ivangarzab.kluvs.designsystem.components.bookcover.BookCoverPlaceholder
import com.ivangarzab.kluvs.designsystem.components.avatars.AvatarStack
import com.ivangarzab.kluvs.designsystem.components.avatars.AvatarStackMember
import com.ivangarzab.kluvs.designsystem.components.buttons.OutlinedButton
import com.ivangarzab.kluvs.designsystem.components.buttons.PrimaryButton
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenu
import com.ivangarzab.kluvs.designsystem.components.menus.ActionMenuItem
import com.ivangarzab.kluvs.designsystem.components.EmptyState
import com.ivangarzab.kluvs.designsystem.components.NoTabData
import com.ivangarzab.kluvs.designsystem.components.progress.OwnProgressRow
import kotlinx.datetime.LocalDateTime

/**
 * Overview tab: active-session summary (book, participation, own progress) and an
 * "up next" discussion teaser. Mirrors web's mobile Overview tab. The club masthead
 * (name, meta row) lives above the tab row in [ClubsScreenContent], not here.
 * The full discussion timeline and end-session flow stay on the Discussions tab.
 */
@Composable
fun OverviewTab(
    modifier: Modifier = Modifier,
    clubDetails: ClubDetails? = null,
    sessionDetails: ActiveSessionDetails? = null,
    ownProgress: OwnProgressInfo? = null,
    userRole: Role? = null,
    members: List<MemberListItemInfo> = emptyList(),
    currentUserId: String? = null,
    onEditSession: () -> Unit = {},
    onEndSession: () -> Unit = {},
    onUpdateProgress: () -> Unit = {},
    onCreateSession: () -> Unit = {},
    onToggleParticipation: (isReading: Boolean) -> Unit = {},
) {
    if (clubDetails == null) {
        NoTabData(
            modifier = modifier,
            text = R.string.no_club_details
        )
        return
    }

    val isAdminOrAbove = userRole == Role.OWNER || userRole == Role.ADMIN
    val currentMemberId = members.find { it.userId == currentUserId }?.memberId

    // A scrollable Column can't force a child to fill the viewport (unbounded height
    // constraints), which is what left NoActiveSessionState stuck at its own small intrinsic
    // size instead of covering the tab. Handling it as an early return — same shape as
    // ActiveSessionTab's no-session branch — lets it size against the real, bounded `modifier`
    // (fillMaxSize from the tab pager) instead.
    if (sessionDetails == null) {
        NoActiveSessionState(
            modifier = modifier,
            isAdminOrAbove = isAdminOrAbove,
            onCreateSession = onCreateSession
        )
        return
    }

    val readingParticipants = sessionDetails.participants.filter { it.isReading }
    val readingMembers = readingParticipants.mapNotNull { participant ->
        members.find { it.memberId == participant.memberId }
    }.map { AvatarStackMember(id = it.memberId, name = it.name, avatarUrl = it.avatarUrl) }
    val isOwnReading = currentMemberId != null &&
        sessionDetails.participants.any { it.memberId == currentMemberId && it.isReading }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SessionSummary(
            sessionDetails = sessionDetails,
            ownProgress = ownProgress,
            readingMembers = readingMembers,
            readingCount = readingParticipants.size,
            totalMemberCount = clubDetails.memberCount,
            isAdminOrAbove = isAdminOrAbove,
            isOwnReading = isOwnReading,
            canToggleParticipation = currentMemberId != null,
            onEditSession = onEditSession,
            onEndSession = onEndSession,
            onUpdateProgress = onUpdateProgress,
            onToggleParticipation = onToggleParticipation
        )

        val nextDiscussion = sessionDetails.discussions.firstOrNull { it.isNext }
        if (nextDiscussion != null) {
            HorizontalDivider(color = KluvsTheme.colors.cardAlt)
            UpNextTeaser(discussion = nextDiscussion)
            HorizontalDivider(color = KluvsTheme.colors.cardAlt)
        }
    }
}

@Composable
private fun SessionSummary(
    sessionDetails: ActiveSessionDetails,
    ownProgress: OwnProgressInfo?,
    readingMembers: List<AvatarStackMember>,
    readingCount: Int,
    totalMemberCount: Int,
    isAdminOrAbove: Boolean,
    isOwnReading: Boolean,
    canToggleParticipation: Boolean,
    onEditSession: () -> Unit,
    onEndSession: () -> Unit,
    onUpdateProgress: () -> Unit,
    onToggleParticipation: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubcomposeAsyncImage(
                model = sessionDetails.book.imageUrl,
                contentDescription = sessionDetails.book.title,
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                loading = { BookCoverPlaceholder(modifier = Modifier.fillMaxWidth()) },
                error = { BookCoverPlaceholder(modifier = Modifier.fillMaxWidth()) }
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.active_session_eyebrow).uppercase(),
                            style = KluvsTheme.typography.eyebrow,
                            color = KluvsTheme.colors.accent
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = sessionDetails.book.title,
                            style = KluvsTheme.typography.title.large.feature(),
                            color = KluvsTheme.colors.content
                        )
                    }
                    if (isAdminOrAbove) {
                        ActionMenu(
                            items = listOf(
                                ActionMenuItem(label = "Edit Session", onClick = onEditSession),
                                ActionMenuItem(label = "End Session", onClick = onEndSession, isDestructive = true)
                            ),
                            contentDescription = "Session options"
                        )
                    }
                }
                Text(
                    text = sessionDetails.book.author,
                    style = KluvsTheme.typography.body.medium,
                    color = KluvsTheme.colors.contentMuted
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (readingMembers.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarStack(members = readingMembers, size = 24.dp)
                    Text(
                        text = stringResource(R.string.x_of_y_reading, readingCount, totalMemberCount),
                        style = KluvsTheme.typography.caption,
                        color = KluvsTheme.colors.contentMuted
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_participants_yet),
                    style = KluvsTheme.typography.body.medium,
                    color = KluvsTheme.colors.contentMuted
                )
            }

            if (canToggleParticipation) {
                OutlinedButton(
                    text = stringResource(if (isOwnReading) R.string.opt_out else R.string.join_this_read),
                    onClick = { onToggleParticipation(!isOwnReading) }
                )
            }
        }

        if (isOwnReading) {
            Spacer(Modifier.height(12.dp))
            // Was a private near-duplicate of the shared OwnProgressRow (design-system
            // primitives migration) — deleted in favor of the real shared component;
            // leftLabelEmphasized reproduces the one real difference (italic discussion count).
            OwnProgressRow(
                percent = ownProgress?.percent,
                statusLabel = ownProgress?.label,
                onUpdateProgress = onUpdateProgress,
                leftLabel = stringResource(
                    R.string.x_of_y_discussions,
                    sessionDetails.discussions.count { it.isPast },
                    sessionDetails.discussions.size
                ),
                leftLabelEmphasized = true,
            )
        }
    }
}

@Composable
private fun NoActiveSessionState(
    isAdminOrAbove: Boolean,
    onCreateSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyState(
        modifier = modifier.fillMaxSize(),
        heading = "No active session yet.",
        body = "Start a session to begin reading together.",
        action = if (isAdminOrAbove) {
            // Primary, not Secondary like other EmptyState actions — starting a session is
            // the single most important next step for a brand-new club.
            { PrimaryButton(text = stringResource(R.string.start_session), onClick = onCreateSession) }
        } else null,
    )
}

@Composable
private fun UpNextTeaser(
    discussion: DiscussionTimelineItemInfo,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.up_next_eyebrow).uppercase(),
                style = KluvsTheme.typography.eyebrow,
                color = KluvsTheme.colors.accent
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = discussion.title,
                style = KluvsTheme.typography.title.medium,
                color = KluvsTheme.colors.content
            )
            Text(
                text = discussion.location,
                style = KluvsTheme.typography.body.medium,
                color = KluvsTheme.colors.contentMuted
            )
        }
        Text(
            text = discussion.date,
            style = KluvsTheme.typography.caption,
            color = KluvsTheme.colors.accent
        )
    }
}

@PreviewLightDark
@Composable
fun Preview_OverviewTab() = KluvsTheme {
    OverviewTab(
        modifier = Modifier
            .background(color = KluvsTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxSize(),
        clubDetails = ClubDetails(
            clubId = "club1",
            clubName = "Test Club Name",
            memberCount = 6,
            foundedYear = "2026",
            currentBook = BookInfo(
                title = "1984",
                author = "George Orwell",
                year = "1948",
                pageCount = 169
            ),
            nextDiscussion = DiscussionInfo(
                title = "Discussion #1",
                location = "Discord",
                formattedDate = "Tomorrow at 7:00 PM"
            )
        ),
        sessionDetails = ActiveSessionDetails(
            sessionId = "s0",
            book = BookInfo(title = "1984", author = "George Orwell", year = "1948", pageCount = 169),
            bookId = "b0",
            dueDate = "January 1st, 2030",
            rawDueDate = null,
            participants = listOf(
                SessionParticipantInfo(memberId = "0", isReading = true),
                SessionParticipantInfo(memberId = "1", isReading = false)
            ),
            discussions = listOf(
                DiscussionTimelineItemInfo(
                    id = "0",
                    title = "Chapters 10-19",
                    location = "Discord voice",
                    date = "Nov 3",
                    rawDate = LocalDateTime(2026, 11, 3, 19, 0),
                    isPast = false,
                    isNext = true
                )
            )
        ),
        ownProgress = OwnProgressInfo(
            progressId = "p0",
            type = ProgressType.PAGE,
            currentPage = 42,
            percentComplete = null,
            isCompleted = false,
            percent = 25,
            label = "42 of 169 pages"
        ),
        members = listOf(
            MemberListItemInfo(memberId = "0", name = "Ana Silva", handle = "@ana", avatarUrl = null, role = Role.OWNER, userId = "u0")
        ),
        currentUserId = "u0",
        userRole = Role.OWNER
    )
}
