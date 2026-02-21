package com.ivangarzab.kluvs.clubs.presentation

import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.model.Role
import com.ivangarzab.kluvs.presentation.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The purpose of this class is to wrap the [ClubDetailsViewModel] for easier
 * use and access on the iOS side.
 */
@Suppress("unused")
class ClubDetailsViewModelHelper : KoinComponent {

    private val viewModel: ClubDetailsViewModel by inject()
    private val coroutineScope: CoroutineScope by inject()

    /**
     * iOS-friendly observation method.
     *
     * Returns a [com.ivangarzab.kluvs.presentation.viewmodels.Closeable] that can be used to cancel the observation.
     */
    fun observeState(callback: (ClubDetailsState) -> Unit): Closeable {
        val job = viewModel.state.onEach { callback(it) }.launchIn(coroutineScope)
        return Closeable { job.cancel() }
    }

    fun loadUserClubs(userId: String) = viewModel.loadUserClubs(userId)

    fun loadClubData(clubId: String) = viewModel.loadClubData(clubId)

    fun selectClub(clubId: String) = viewModel.selectClub(clubId)

    fun refresh() = viewModel.refresh()

    // General tab
    fun onUpdateClubName(newName: String) = viewModel.onUpdateClubName(newName)
    fun onDeleteClub() = viewModel.onDeleteClub()

    // Session tab
    fun onCreateSession(book: Book, dueDate: LocalDateTime?) = viewModel.onCreateSession(book, dueDate)
    fun onUpdateSession(book: Book?, dueDate: LocalDateTime?) = viewModel.onUpdateSession(book, dueDate)
    fun onDeleteSession() = viewModel.onDeleteSession()

    // Discussion operations
    fun onCreateDiscussion(title: String, location: String, date: LocalDateTime) =
        viewModel.onCreateDiscussion(title, location, date)
    fun onUpdateDiscussion(discussionId: String, title: String?, location: String?, date: LocalDateTime?) =
        viewModel.onUpdateDiscussion(discussionId, title, location, date)
    fun onDeleteDiscussion(discussionId: String) = viewModel.onDeleteDiscussion(discussionId)

    // Member operations
    fun onUpdateMemberRole(memberId: String, currentMemberId: String, newRole: Role) =
        viewModel.onUpdateMemberRole(memberId, currentMemberId, newRole)
    fun onRemoveMember(memberId: String, currentMemberId: String) =
        viewModel.onRemoveMember(memberId, currentMemberId)

    // UI event consumption
    fun onConsumeOperationResult() = viewModel.onConsumeOperationResult()
}
