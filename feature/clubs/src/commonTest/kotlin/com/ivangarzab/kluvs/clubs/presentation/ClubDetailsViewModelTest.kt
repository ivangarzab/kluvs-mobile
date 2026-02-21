package com.ivangarzab.kluvs.clubs.presentation

import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.model.Book
import com.ivangarzab.kluvs.model.Club
import com.ivangarzab.kluvs.model.Discussion
import com.ivangarzab.kluvs.model.Member
import com.ivangarzab.kluvs.model.Session
import com.ivangarzab.kluvs.clubs.domain.GetActiveSessionUseCase
import com.ivangarzab.kluvs.clubs.domain.GetClubDetailsUseCase
import com.ivangarzab.kluvs.clubs.domain.GetClubMembersUseCase
import com.ivangarzab.kluvs.clubs.domain.GetMemberClubsUseCase
import com.ivangarzab.kluvs.data.repositories.AvatarRepository
import com.ivangarzab.kluvs.model.ClubMember
import com.ivangarzab.kluvs.presentation.util.FormatDateTimeUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ClubDetailsViewModelTest {

    private lateinit var clubRepository: ClubRepository
    private lateinit var memberRepository: MemberRepository
    private lateinit var avatarRepository: AvatarRepository
    private lateinit var getClubDetails: GetClubDetailsUseCase
    private lateinit var getActiveSession: GetActiveSessionUseCase
    private lateinit var getClubMembers: GetClubMembersUseCase
    private lateinit var getMemberClubs: GetMemberClubsUseCase
    private lateinit var viewModel: ClubDetailsViewModel

    private val formatDateTime = FormatDateTimeUseCase()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clubRepository = mock<ClubRepository>()
        memberRepository = mock<MemberRepository>()
        avatarRepository = mock<AvatarRepository>()

        // Use REAL UseCases with mocked repositories
        getClubDetails = GetClubDetailsUseCase(clubRepository, formatDateTime)
        getActiveSession = GetActiveSessionUseCase(clubRepository, formatDateTime)
        getClubMembers = GetClubMembersUseCase(clubRepository, avatarRepository)
        getMemberClubs = GetMemberClubsUseCase(memberRepository)

        viewModel = ClubDetailsViewModel(getClubDetails, getActiveSession, getClubMembers, getMemberClubs)

        every { avatarRepository.getAvatarUrl(null) } returns null
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading with no data`() {
        // Then
        val state = viewModel.state.value
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertNull(state.currentClubDetails)
        assertNull(state.activeSession)
        assertTrue(state.members.isEmpty())
    }

    @Test
    fun `loadClubData updates state with success data from all UseCases`() = runTest {
        // Given
        val clubId = "club-123"
        val book = Book("book-1", "The Hobbit", "Tolkien", null, 1937, null)
        val futureDiscussion = Discussion(
            id = "d1",
            sessionId = "s1",
            title = "Chapter 1",
            date = LocalDateTime(2026, 1, 15, 19, 0),
            location = "Discord"
        )
        val activeSession = Session(
            id = "session-1",
            clubId = clubId,
            book = book,
            dueDate = LocalDateTime(2026, 3, 15, 0, 0),
            discussions = listOf(futureDiscussion)
        )
        val members = listOf(
            ClubMember(role = "owner", Member(id = "m1", userId = "u1", name = "Alice", booksRead = 5, clubs = null)),
            ClubMember(role = "member", Member(id = "m2", userId = "u2", name = "Bob", booksRead = 3, clubs = null))
        )
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = members,
            activeSession = activeSession,
            pastSessions = emptyList(),
            shameList = emptyList()
        )

        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.loadClubData(clubId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("Test Club", state.currentClubDetails?.clubName)
        assertEquals(2, state.currentClubDetails?.memberCount)
        assertEquals("session-1", state.activeSession?.sessionId)
        assertEquals(2, state.members.size)
        assertEquals("Alice", state.members[0].name)
    }

    @Test
    fun `loadClubData sets loading true initially then false after completion`() = runTest {
        // Given
        val clubId = "club-123"
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.loadClubData(clubId)

        // Then - After completion, loading should be false
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadClubData handles error from repository`() = runTest {
        // Given
        val clubId = "club-123"
        val errorMessage = "Failed to fetch club"
        everySuspend { clubRepository.getClub(clubId) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadClubData(clubId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertNull(state.currentClubDetails)
        assertNull(state.activeSession)
        assertTrue(state.members.isEmpty())
    }

    @Test
    fun `loadClubData handles club with no active session`() = runTest {
        // Given
        val clubId = "club-123"
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.loadClubData(clubId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("Test Club", state.currentClubDetails?.clubName)
        assertNull(state.activeSession)
        assertTrue(state.members.isEmpty())
    }

    @Test
    fun `loadClubData calculates member count correctly`() = runTest {
        // Given
        val clubId = "club-123"
        val members = listOf(
            ClubMember(role = "owner", Member(id = "m1", userId = "u1", name = "Alice", booksRead = 5, clubs = null)),
            ClubMember(role = "owner", Member(id = "m2", userId = "u2", name = "Bob", booksRead = 3, clubs = null)),
            ClubMember(role = "owner", Member(id = "m3", userId = "u3", name = "Charlie", booksRead = 4, clubs = null)),
        )
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = members,
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.loadClubData(clubId)

        // Then
        assertEquals(3, viewModel.state.value.currentClubDetails?.memberCount)
    }

    @Test
    fun `refresh reloads data with same clubId`() = runTest {
        // Given
        val clubId = "club-123"
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // Load initial data
        viewModel.loadClubData(clubId)

        // When
        viewModel.refresh()

        // Then - State should be refreshed
        val refreshedState = viewModel.state.value
        assertEquals(clubId, refreshedState.currentClubDetails?.clubId)
        assertFalse(refreshedState.isLoading)
    }

    @Test
    fun `refresh does nothing when no clubId has been loaded`() = runTest {
        // Given - No data loaded yet
        val initialState = viewModel.state.value

        // When
        viewModel.refresh()

        // Then - State should remain unchanged (still in initial loading state)
        val afterRefreshState = viewModel.state.value
        assertEquals(initialState.isLoading, afterRefreshState.isLoading)
        assertEquals(initialState.currentClubDetails, afterRefreshState.currentClubDetails)
    }

    @Test
    fun `loadClubData clears previous error before loading`() = runTest {
        // Given
        val clubId = "club-123"
        everySuspend { clubRepository.getClub(clubId) } returns Result.failure(Exception("Error"))

        // Load data with error
        viewModel.loadClubData(clubId)
        assertEquals("Error", viewModel.state.value.error)

        // Given - Now succeed
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When - Load again
        viewModel.loadClubData(clubId)

        // Then - Error should be cleared
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `selectClub updates selectedClubId in state`() = runTest {
        // Given
        val clubId = "club-456"
        val club = Club(
            id = clubId,
            name = "New Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.selectClub(clubId)

        // Then
        assertEquals(clubId, viewModel.state.value.selectedClubId)
    }

    @Test
    fun `selectClub triggers data load for the new club`() = runTest {
        // Given
        val clubId = "club-789"
        val club = Club(
            id = clubId,
            name = "Another Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.selectClub(clubId)

        // Then - club data should be loaded
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Another Club", state.currentClubDetails?.clubName)
    }

    @Test
    fun `selectedClubId is set after loadUserClubs completes`() = runTest {
        // Given
        val userId = "user-1"
        val clubId = "club-123"
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        val member = Member(
            id = "m1",
            userId = userId,
            name = "Alice",
            booksRead = 0,
            clubs = listOf(club)
        )
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.loadUserClubs(userId)

        // Then
        assertNotNull(viewModel.state.value.selectedClubId)
        assertEquals(clubId, viewModel.state.value.selectedClubId)
    }

    @Test
    fun `selectedClubId persists through loading cycles`() = runTest {
        // Given
        val clubId = "club-123"
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = null,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // Load data first time
        viewModel.selectClub(clubId)
        assertEquals(clubId, viewModel.state.value.selectedClubId)

        // When - reload (refresh)
        viewModel.refresh()

        // Then - selectedClubId should still be set
        assertEquals(clubId, viewModel.state.value.selectedClubId)
    }

    @Test
    fun `loadClubData handles discussions timeline correctly`() = runTest {
        // Given
        val clubId = "club-123"
        val book = Book("book-1", "The Hobbit", "Tolkien", null, 1937, null)
        val pastDiscussion = Discussion(
            id = "d1",
            sessionId = "s1",
            title = "Chapter 1",
            date = LocalDateTime(2024, 1, 1, 19, 0),
            location = "Discord"
        )
        val futureDiscussion = Discussion(
            id = "d2",
            sessionId = "s1",
            title = "Chapter 2",
            date = LocalDateTime(2032, 2, 1, 19, 0),
            location = "Discord"
        )
        val activeSession = Session(
            id = "session-1",
            clubId = clubId,
            book = book,
            dueDate = LocalDateTime(2026, 3, 15, 0, 0),
            discussions = listOf(pastDiscussion, futureDiscussion)
        )
        val club = Club(
            id = clubId,
            name = "Test Club",
            serverId = null,
            discordChannel = null,
            members = emptyList(),
            activeSession = activeSession,
            pastSessions = emptyList(),
            shameList = emptyList()
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(club)

        // When
        viewModel.loadClubData(clubId)

        // Then
        val timeline = viewModel.state.value.activeSession?.discussions
        assertEquals(2, timeline?.size)
        assertTrue(timeline?.get(0)?.isPast == true)
        assertTrue(timeline?.get(1)?.isNext == true)
    }
}