package com.ivangarzab.kluvs.presentation.viewmodels.member

import com.ivangarzab.kluvs.data.auth.AuthRepository
import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.domain.models.Book
import com.ivangarzab.kluvs.domain.models.Club
import com.ivangarzab.kluvs.domain.models.Discussion
import com.ivangarzab.kluvs.domain.models.Member
import com.ivangarzab.kluvs.domain.models.Session
import com.ivangarzab.kluvs.domain.usecases.auth.SignOutUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetCurrentUserProfileUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetCurrentlyReadingBooksUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetUserStatisticsUseCase
import com.ivangarzab.kluvs.presentation.util.FormatDateTimeUseCase
import dev.mokkery.answering.returns
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MeViewModelTest {

    private lateinit var memberRepository: MemberRepository
    private lateinit var clubRepository: ClubRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var getCurrentUserProfile: GetCurrentUserProfileUseCase
    private lateinit var getUserStatistics: GetUserStatisticsUseCase
    private lateinit var getCurrentlyReadingBooks: GetCurrentlyReadingBooksUseCase
    private lateinit var signOut: SignOutUseCase
    private lateinit var viewModel: MeViewModel

    private val formatDateTime = FormatDateTimeUseCase()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        memberRepository = mock<MemberRepository>()
        clubRepository = mock<ClubRepository>()
        authRepository = mock<AuthRepository>()

        // Use REAL UseCases with mocked repositories
        getCurrentUserProfile = GetCurrentUserProfileUseCase(memberRepository, formatDateTime)
        getUserStatistics = GetUserStatisticsUseCase(memberRepository)
        getCurrentlyReadingBooks = GetCurrentlyReadingBooksUseCase(memberRepository, clubRepository, formatDateTime)
        signOut = SignOutUseCase(authRepository)

        viewModel = MeViewModel(getCurrentUserProfile, getUserStatistics, getCurrentlyReadingBooks, signOut)
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
        assertNull(state.profile)
        assertNull(state.statistics)
        assertTrue(state.currentlyReading.isEmpty())
    }

    @Test
    fun `loadUserData updates state with success data from all UseCases`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-1",
            userId = userId,
            name = "Alice Johnson",
            points = 150,
            booksRead = 12,
            clubs = listOf(
                Club("club-1", "Fantasy Readers", null, null, null, emptyList(), null, null, emptyList()),
                Club("club-2", "Sci-Fi Club", null, null, null, emptyList(), null, null, emptyList()),
                Club("club-3", "Mystery Book Club", null, null, null, emptyList(), null, null, emptyList())
            )
        )

        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // Mock club details for currently reading books
        val book1 = Book("book-1", "The Hobbit", "Tolkien", null, 1937, null)
        val book2 = Book("book-2", "Dune", "Herbert", null, 1965, null)

        val session1 = Session(
            id = "s1",
            clubId = "club-1",
            book = book1,
            dueDate = LocalDateTime(2026, 3, 15, 0, 0),
            discussions = listOf(
                Discussion("d1", "s1", "Chapter 1", LocalDateTime(2024, 1, 1, 19, 0), null),
                Discussion("d2", "s1", "Chapter 2", LocalDateTime(2026, 2, 1, 19, 0), null)
            )
        )
        val session2 = Session(
            id = "s2",
            clubId = "club-2",
            book = book2,
            dueDate = LocalDateTime(2026, 4, 1, 0, 0),
            discussions = listOf(
                Discussion("d3", "s2", "Part 1", LocalDateTime(2026, 3, 1, 19, 0), null),
                Discussion("d4", "s2", "Part 2", LocalDateTime(2026, 3, 15, 19, 0), null),
                Discussion("d5", "s2", "Part 3", LocalDateTime(2026, 3, 29, 19, 0), null),
                Discussion("d6", "s2", "Part 4", LocalDateTime(2026, 4, 12, 19, 0), null)
            )
        )

        val club1 = Club("club-1", "Fantasy Readers", null, null, null, emptyList(), null, session1, emptyList())
        val club2 = Club("club-2", "Sci-Fi Club", null, null, null, emptyList(), null, session2, emptyList())
        val club3 = Club("club-3", "Mystery Book Club", null, null, null, emptyList(), null, null, emptyList())

        everySuspend { clubRepository.getClub("club-1") } returns Result.success(club1)
        everySuspend { clubRepository.getClub("club-2") } returns Result.success(club2)
        everySuspend { clubRepository.getClub("club-3") } returns Result.success(club3)

        // When
        viewModel.loadUserData(userId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)

        // Profile
        assertEquals("member-1", state.profile?.memberId)
        assertEquals("Alice Johnson", state.profile?.name)
        assertEquals("@alicejohnson", state.profile?.handle)

        // Statistics
        assertEquals(3, state.statistics?.clubsCount)
        assertEquals(150, state.statistics?.totalPoints)
        assertEquals(12, state.statistics?.booksRead)

        // Currently reading books
        assertEquals(2, state.currentlyReading.size)
        assertEquals("The Hobbit", state.currentlyReading[0].bookTitle)
        assertEquals("Fantasy Readers", state.currentlyReading[0].clubName)
        assertEquals(0.5f, state.currentlyReading[0].progress) // 1 of 2 discussions complete
        assertEquals("Dune", state.currentlyReading[1].bookTitle)
        assertEquals(0.0f, state.currentlyReading[1].progress) // 0 of 4 discussions complete
    }

    @Test
    fun `loadUserData handles error from member repository`() = runTest {
        // Given
        val userId = "user-123"
        val errorMessage = "Failed to fetch member"
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadUserData(userId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertNull(state.profile)
        assertNull(state.statistics)
        assertTrue(state.currentlyReading.isEmpty())
    }

    @Test
    fun `loadUserData handles member with no clubs`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-1",
            userId = userId,
            name = "New User",
            points = 0,
            booksRead = 0,
            clubs = emptyList()
        )

        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // When
        viewModel.loadUserData(userId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("New User", state.profile?.name)
        assertEquals(0, state.statistics?.clubsCount)
        assertTrue(state.currentlyReading.isEmpty())
    }

    @Test
    fun `loadUserData calculates progress based on completed discussions`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-1",
            userId = userId,
            name = "Alice",
            points = 100,
            booksRead = 5,
            clubs = listOf(
                Club("club-1", "Test Club", null, null, null, emptyList(), null, null, emptyList())
            )
        )

        val book = Book("book-1", "Test Book", "Author", null, 2024, null)
        val session = Session(
            id = "s1",
            clubId = "club-1",
            book = book,
            dueDate = LocalDateTime(2026, 12, 31, 0, 0),
            discussions = listOf(
                Discussion("d1", "s1", "Part 1", LocalDateTime(2024, 1, 1, 19, 0), null), // Past
                Discussion("d2", "s1", "Part 2", LocalDateTime(2024, 2, 1, 19, 0), null), // Past
                Discussion("d3", "s1", "Part 3", LocalDateTime(2024, 3, 1, 19, 0), null), // Past
                Discussion("d4", "s1", "Part 4", LocalDateTime(2026, 4, 1, 19, 0), null)  // Future
            )
        )

        val club = Club("club-1", "Test Club", null, null, null, emptyList(), null, session, emptyList())

        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)
        everySuspend { clubRepository.getClub("club-1") } returns Result.success(club)

        // When
        viewModel.loadUserData(userId)

        // Then
        val reading = viewModel.state.value.currentlyReading
        assertEquals(1, reading.size)
        assertEquals(0.75f, reading[0].progress) // 3 of 4 discussions complete
    }

    @Test
    fun `refresh reloads data with same userId`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-1",
            userId = userId,
            name = "Alice",
            points = 100,
            booksRead = 5,
            clubs = emptyList()
        )

        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // Load initial data
        viewModel.loadUserData(userId)

        // When
        viewModel.refresh()

        // Then - State should be refreshed
        val refreshedState = viewModel.state.value
        assertEquals("member-1", refreshedState.profile?.memberId)
        assertFalse(refreshedState.isLoading)
    }

    @Test
    fun `refresh does nothing when no userId has been loaded`() = runTest {
        // Given - No data loaded yet
        val initialState = viewModel.state.value

        // When
        viewModel.refresh()

        // Then - State should remain unchanged (still in initial loading state)
        val afterRefreshState = viewModel.state.value
        assertEquals(initialState.isLoading, afterRefreshState.isLoading)
        assertEquals(initialState.profile, afterRefreshState.profile)
    }

    @Test
    fun `loadUserData clears previous error before loading`() = runTest {
        // Given
        val userId = "user-123"
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.failure(Exception("Error"))

        // Load data with error
        viewModel.loadUserData(userId)
        assertEquals("Error", viewModel.state.value.error)

        // Given - Now succeed
        val member = Member("member-1", "Alice", null, 100, 5, userId, null, null, emptyList(), null)
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // When - Load again
        viewModel.loadUserData(userId)

        // Then - Error should be cleared
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `loadUserData generates handle from member name`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member("member-1", "John Doe", null, 50, 2, userId, null, null, emptyList(), null)
        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)

        // When
        viewModel.loadUserData(userId)

        // Then
        assertEquals("@johndoe", viewModel.state.value.profile?.handle)
    }

    @Test
    fun `loadUserData handles clubs with no active session`() = runTest {
        // Given
        val userId = "user-123"
        val member = Member(
            id = "member-1",
            userId = userId,
            name = "Alice",
            points = 100,
            booksRead = 5,
            clubs = listOf(
                Club("club-1", "Inactive Club", null, null, null, emptyList(), null, null, emptyList())
            )
        )

        val club = Club("club-1", "Inactive Club", null, null, null, emptyList(), null, null, emptyList())

        everySuspend { memberRepository.getMemberByUserId(userId) } returns Result.success(member)
        everySuspend { clubRepository.getClub("club-1") } returns Result.success(club)

        // When
        viewModel.loadUserData(userId)

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("Alice", state.profile?.name)
        assertEquals(1, state.statistics?.clubsCount)
        assertTrue(state.currentlyReading.isEmpty()) // No active sessions
    }
}
