package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.kluvs.data.remote.api.SessionService
import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.data.remote.dtos.ClubDto
import com.ivangarzab.kluvs.data.remote.dtos.CreateSessionRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.DeleteResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.DiscussionDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionSuccessResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionUpdatesDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateSessionRequestDto
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionRemoteDataSourceTest {

    private lateinit var sessionService: SessionService
    private lateinit var dataSource: SessionRemoteDataSource

    @BeforeTest
    fun setup() {
        sessionService = mock<SessionService>()
        dataSource = SessionRemoteDataSourceImpl(sessionService)
    }

    @Test
    fun `getSession success returns mapped Session domain model`() = runTest {
        // Given: Service returns SessionResponseDto
        val dto = SessionResponseDto(
            id = "session-1",
            club = ClubDto("club-1", "Book Club", "123456789", "server-1"),
            book = BookDto("book-1", "The Hobbit", "Tolkien"),
            due_date = "2024-12-31T23:59:59",
            shame_list = emptyList(),
            discussions = listOf(
                DiscussionDto("disc-1", "session-1", "Chapter 1", "2024-06-15T18:00:00", "Discord")
            )
        )

        everySuspend { sessionService.get("session-1") } returns dto

        // When: Getting session
        val result = dataSource.getSession("session-1")

        // Then: Result is success with mapped domain model
        assertTrue(result.isSuccess)
        val session = result.getOrNull()!!
        assertEquals("session-1", session.id)
        assertEquals("club-1", session.clubId)
        assertEquals("The Hobbit", session.book.title)
        assertEquals(1, session.discussions.size)

        verifySuspend { sessionService.get("session-1") }
    }

    @Test
    fun `getSession failure returns Result failure`() = runTest {
        // Given: Service throws exception
        val exception = Exception("Session not found")
        everySuspend { sessionService.get("invalid") } throws exception

        // When: Getting session
        val result = dataSource.getSession("invalid")

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        verifySuspend { sessionService.get("invalid") }
    }

    @Test
    fun `createSession success returns created Session`() = runTest {
        // Given: Service returns success response
        val request = CreateSessionRequestDto(
            club_id = "club-1",
            book = BookDto(null, "New Book", "New Author"),
            due_date = "2025-06-30T00:00:00"
        )

        val responseDto = SessionSuccessResponseDto(
            success = true,
            message = "Created",
            session = SessionDto(
                id = "session-2",
                club_id = "club-1",
                book = BookDto("book-2", "New Book", "New Author"),
                due_date = "2025-06-30T00:00:00",
                discussions = emptyList()
            ),
            updates = SessionUpdatesDto(book = true, session = true, discussions = false)
        )

        everySuspend { sessionService.create(request) } returns responseDto

        // When: Creating session
        val result = dataSource.createSession(request)

        // Then: Result is success
        assertTrue(result.isSuccess)
        val session = result.getOrNull()!!
        assertEquals("session-2", session.id)
        assertEquals("New Book", session.book.title)

        verifySuspend { sessionService.create(request) }
    }

    @Test
    fun `createSession with null session in response returns failure`() = runTest {
        // Given: Service returns response without session
        val request = CreateSessionRequestDto(
            club_id = "club-1",
            book = BookDto(null, "Book", "Author")
        )

        val responseDto = SessionSuccessResponseDto(
            success = true,
            message = "Created but no session returned",
            session = null,
            updates = null
        )

        everySuspend { sessionService.create(request) } returns responseDto

        // When: Creating session
        val result = dataSource.createSession(request)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("no session returned") == true)

        verifySuspend { sessionService.create(request) }
    }

    @Test
    fun `updateSession success returns updated Session`() = runTest {
        // Given: Service returns success response
        val request = UpdateSessionRequestDto(
            id = "session-1",
            due_date = "2025-12-31T23:59:59"
        )

        val responseDto = SessionSuccessResponseDto(
            success = true,
            message = "Updated",
            session = SessionDto(
                id = "session-1",
                club_id = "club-1",
                book = BookDto("book-1", "The Hobbit", "Tolkien"),
                due_date = "2025-12-31T23:59:59",
                discussions = emptyList()
            ),
            updates = SessionUpdatesDto(book = false, session = true, discussions = false)
        )

        everySuspend { sessionService.update(request) } returns responseDto

        // When: Updating session
        val result = dataSource.updateSession(request)

        // Then: Result is success
        assertTrue(result.isSuccess)
        assertEquals("session-1", result.getOrNull()?.id)

        verifySuspend { sessionService.update(request) }
    }

    @Test
    fun `deleteSession success returns success message`() = runTest {
        // Given: Service returns success response
        val response = DeleteResponseDto(
            success = true,
            message = "Session deleted"
        )

        everySuspend { sessionService.delete("session-1") } returns response

        // When: Deleting session
        val result = dataSource.deleteSession("session-1")

        // Then: Result is success
        assertTrue(result.isSuccess)
        assertEquals("Session deleted", result.getOrNull())

        verifySuspend { sessionService.delete("session-1") }
    }

    @Test
    fun `deleteSession with success false returns failure`() = runTest {
        // Given: Service returns failure response
        val response = DeleteResponseDto(
            success = false,
            message = "Cannot delete active session"
        )

        everySuspend { sessionService.delete("session-1") } returns response

        // When: Deleting session
        val result = dataSource.deleteSession("session-1")

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Cannot delete active session") == true)

        verifySuspend { sessionService.delete("session-1") }
    }
}
