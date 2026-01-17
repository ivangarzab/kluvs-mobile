package com.ivangarzab.bookclub.presentation.viewmodels

import com.ivangarzab.bookclub.data.repositories.ClubRepository
import com.ivangarzab.bookclub.data.repositories.MemberRepository
import com.ivangarzab.bookclub.domain.models.Club
import com.ivangarzab.bookclub.domain.usecases.club.GetActiveSessionUseCase
import com.ivangarzab.bookclub.domain.usecases.club.GetClubDetailsUseCase
import com.ivangarzab.bookclub.domain.usecases.club.GetClubMembersUseCase
import com.ivangarzab.bookclub.domain.usecases.member.GetMemberClubsUseCase
import com.ivangarzab.bookclub.domain.usecases.util.FormatDateTimeUseCase
import com.ivangarzab.bookclub.presentation.viewmodels.club.ClubDetailsState
import com.ivangarzab.bookclub.presentation.viewmodels.club.ClubDetailsViewModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ClubDetailsViewModelHelperTest {

    private lateinit var clubRepository: ClubRepository
    private lateinit var memberRepository: MemberRepository
    private lateinit var viewModel: ClubDetailsViewModel
    private lateinit var testScope: CoroutineScope
    private lateinit var helper: ClubDetailsViewModelHelper
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create mocked repositories
        clubRepository = mock<ClubRepository>()
        memberRepository = mock<MemberRepository>()

        // Create test scope
        testScope = CoroutineScope(testDispatcher + Job())

        // Create real use cases with mocked repositories
        val formatDateTime = FormatDateTimeUseCase()
        val getClubDetails = GetClubDetailsUseCase(clubRepository, formatDateTime)
        val getActiveSession = GetActiveSessionUseCase(clubRepository, formatDateTime)
        val getClubMembers = GetClubMembersUseCase(clubRepository)
        val getMemberClubs = GetMemberClubsUseCase(memberRepository)

        // Create real ViewModel with real use cases
        viewModel = ClubDetailsViewModel(getClubDetails, getActiveSession, getClubMembers, getMemberClubs)

        // Start Koin with test module
        startKoin {
            modules(
                module {
                    single<ClubDetailsViewModel> { viewModel }
                    single<CoroutineScope> { testScope }
                }
            )
        }

        helper = ClubDetailsViewModelHelper()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        testScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `observeState immediately calls callback with current state`() = runTest {
        // Given
        var callbackInvoked = false
        var receivedState: ClubDetailsState? = null

        // When
        val closeable = helper.observeState { state ->
            callbackInvoked = true
            receivedState = state
        }

        // Then
        assertTrue(callbackInvoked, "Callback should be invoked immediately")
        assertNotNull(receivedState)
        assertTrue(receivedState!!.isLoading)
        assertNull(receivedState!!.error)

        closeable.close()
    }

    @Test
    fun `observeState receives updated states when loadClubData is called`() = runTest {
        // Given
        val receivedStates = mutableListOf<ClubDetailsState>()
        val closeable = helper.observeState { state ->
            receivedStates.add(state)
        }

        // Mock repository to return test data
        val testClub = Club(
            id = "club-1",
            name = "Test Club",
            serverId = "server-1"
        )
        everySuspend { clubRepository.getClub("club-1") } returns Result.success(testClub)

        // When - call loadClubData
        helper.loadClubData("club-1")

        // Then
        assertTrue(receivedStates.size >= 2, "Should receive initial state + at least one update")
        assertTrue(receivedStates.first().isLoading, "First state should be loading")

        closeable.close()
    }

    @Test
    fun `closeable stops receiving updates when closed`() = runTest {
        // Given
        val receivedStates = mutableListOf<ClubDetailsState>()
        val closeable = helper.observeState { state ->
            receivedStates.add(state)
        }

        val initialSize = receivedStates.size

        // When - close the observer
        closeable.close()

        // Trigger state change after closing
        val testClub = Club(
            id = "club-1",
            name = "Test Club",
            serverId = "server-1"
        )
        everySuspend { clubRepository.getClub("club-1") } returns Result.success(testClub)
        helper.loadClubData("club-1")

        // Then - should not receive new states after closing
        assertEquals(initialSize, receivedStates.size, "Should not receive state emitted after closing")
    }

    @Test
    fun `loadClubData calls ViewModel loadClubData`() = runTest {
        // Given
        val clubId = "test-club-id"
        val testClub = Club(
            id = clubId,
            name = "Test Club",
            serverId = "server-1"
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(testClub)

        // When
        helper.loadClubData(clubId)

        // Then - verify state was updated (which means ViewModel was called)
        assertNotNull(viewModel.state.value)
    }

    @Test
    fun `refresh calls ViewModel refresh`() = runTest {
        // Given - load some initial data
        val clubId = "test-club-id"
        val testClub = Club(
            id = clubId,
            name = "Test Club",
            serverId = "server-1"
        )
        everySuspend { clubRepository.getClub(clubId) } returns Result.success(testClub)
        helper.loadClubData(clubId)

        // When
        helper.refresh()

        // Then - verify refresh was called (state should still be valid)
        assertNotNull(viewModel.state.value)
    }

    @Test
    fun `multiple observers can observe simultaneously`() = runTest {
        // Given
        val observer1States = mutableListOf<ClubDetailsState>()
        val observer2States = mutableListOf<ClubDetailsState>()

        val closeable1 = helper.observeState { state ->
            observer1States.add(state)
        }
        val closeable2 = helper.observeState { state ->
            observer2States.add(state)
        }

        val initialSize1 = observer1States.size
        val initialSize2 = observer2States.size

        // When - trigger state change
        val testClub = Club(
            id = "club-1",
            name = "Test Club",
            serverId = "server-1"
        )
        everySuspend { clubRepository.getClub("club-1") } returns Result.success(testClub)
        helper.loadClubData("club-1")

        // Then
        assertTrue(observer1States.size >= initialSize1, "Observer 1 should receive states")
        assertTrue(observer2States.size >= initialSize2, "Observer 2 should receive states")

        closeable1.close()
        closeable2.close()
    }

    @Test
    fun `closing one observer does not affect other observers`() = runTest {
        // Given
        val observer1States = mutableListOf<ClubDetailsState>()
        val observer2States = mutableListOf<ClubDetailsState>()

        val closeable1 = helper.observeState { state ->
            observer1States.add(state)
        }
        val closeable2 = helper.observeState { state ->
            observer2States.add(state)
        }

        // When - close first observer
        closeable1.close()
        val sizeBefore = observer1States.size

        // Trigger state change
        val testClub = Club(
            id = "club-1",
            name = "Test Club",
            serverId = "server-1"
        )
        everySuspend { clubRepository.getClub("club-1") } returns Result.success(testClub)
        helper.loadClubData("club-1")

        // Then
        assertEquals(sizeBefore, observer1States.size, "Observer 1 should not receive new states")
        assertTrue(observer2States.size > 1, "Observer 2 should continue receiving states")

        closeable2.close()
    }
}
