package com.ivangarzab.kluvs.data.remote.source

import com.ivangarzab.kluvs.data.remote.api.ServerService
import com.ivangarzab.kluvs.data.remote.dtos.BookDto
import com.ivangarzab.kluvs.data.remote.dtos.CreateServerRequestDto
import com.ivangarzab.kluvs.data.remote.dtos.DeleteResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerClubDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ServerSuccessResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.ServersResponseDto
import com.ivangarzab.kluvs.data.remote.dtos.SessionDto
import com.ivangarzab.kluvs.data.remote.dtos.UpdateServerRequestDto
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

class ServerRemoteDataSourceTest {

    private lateinit var serverService: ServerService
    private lateinit var dataSource: ServerRemoteDataSource

    @BeforeTest
    fun setup() {
        serverService = mock<ServerService>()
        dataSource = ServerRemoteDataSourceImpl(serverService)
    }

    @Test
    fun `getAllServers success returns list of mapped Servers`() = runTest {
        // Given: Service returns ServersResponseDto
        val dto = ServersResponseDto(
            servers = listOf(
                ServerResponseDto(
                    id = "server-1",
                    name = "Production",
                    clubs = listOf(
                        ServerClubDto(
                            id = "club-1",
                            name = "Fiction",
                            discord_channel = "123456789",
                            founded_date = null,
                            member_count = 10,
                            latest_session = null
                        )
                    )
                ),
                ServerResponseDto(
                    id = "server-2",
                    name = "Test",
                    clubs = emptyList()
                )
            )
        )

        everySuspend { serverService.getAll() } returns dto

        // When: Getting all servers
        val result = dataSource.getAllServers()

        // Then: Result is success with list of servers
        assertTrue(result.isSuccess)
        val servers = result.getOrNull()!!
        assertEquals(2, servers.size)
        assertEquals("Production", servers[0].name)
        assertEquals(1, servers[0].clubs?.size)
        assertEquals("Test", servers[1].name)
        assertEquals(0, servers[1].clubs?.size)

        verifySuspend { serverService.getAll() }
    }

    @Test
    fun `getAllServers failure returns Result failure`() = runTest {
        // Given: Service throws exception
        val exception = Exception("Network error")
        everySuspend { serverService.getAll() } throws exception

        // When: Getting all servers
        val result = dataSource.getAllServers()

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        verifySuspend { serverService.getAll() }
    }

    @Test
    fun `getServer success returns mapped Server with clubs`() = runTest {
        // Given: Service returns ServerResponseDto
        val dto = ServerResponseDto(
            id = "server-1",
            name = "Main Server",
            clubs = listOf(
                ServerClubDto(
                    id = "club-1",
                    name = "Book Club",
                    discord_channel = "123456789",
                    member_count = 15,
                    latest_session = SessionDto(
                        id = "session-1",
                        club_id = "club-1",
                        book = BookDto("book-1", "Great Book", "Author"),
                        due_date = null,
                        discussions = emptyList()
                    )
                )
            )
        )

        everySuspend { serverService.get("server-1") } returns dto

        // When: Getting server
        val result = dataSource.getServer("server-1")

        // Then: Result is success with clubs
        assertTrue(result.isSuccess)
        val server = result.getOrNull()!!
        assertEquals("server-1", server.id)
        assertEquals("Main Server", server.name)
        assertEquals(1, server.clubs?.size)

        val club = server.clubs?.first()
        assertEquals("Book Club", club?.name)
        assertEquals("session-1", club?.activeSession?.id)

        verifySuspend { serverService.get("server-1") }
    }

    @Test
    fun `getServer failure returns Result failure`() = runTest {
        // Given: Service throws exception
        val exception = Exception("Server not found")
        everySuspend { serverService.get("invalid") } throws exception

        // When: Getting server
        val result = dataSource.getServer("invalid")

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        verifySuspend { serverService.get("invalid") }
    }

    @Test
    fun `createServer success returns created Server`() = runTest {
        // Given: Service returns success response
        val request = CreateServerRequestDto(
            id = "server-3",
            name = "New Server"
        )

        val responseDto = ServerSuccessResponseDto(
            success = true,
            message = "Created",
            server = ServerDto("server-3", "New Server")
        )

        everySuspend { serverService.create(request) } returns responseDto

        // When: Creating server
        val result = dataSource.createServer(request)

        // Then: Result is success
        assertTrue(result.isSuccess)
        val server = result.getOrNull()!!
        assertEquals("server-3", server.id)
        assertEquals("New Server", server.name)

        verifySuspend { serverService.create(request) }
    }

    @Test
    fun `updateServer success returns updated Server`() = runTest {
        // Given: Service returns success response
        val request = UpdateServerRequestDto(
            id = "server-1",
            name = "Updated Name"
        )

        val responseDto = ServerSuccessResponseDto(
            success = true,
            message = "Updated",
            server = ServerDto("server-1", "Updated Name")
        )

        everySuspend { serverService.update(request) } returns responseDto

        // When: Updating server
        val result = dataSource.updateServer(request)

        // Then: Result is success
        assertTrue(result.isSuccess)
        assertEquals("Updated Name", result.getOrNull()?.name)

        verifySuspend { serverService.update(request) }
    }

    @Test
    fun `deleteServer success returns success message`() = runTest {
        // Given: Service returns success response
        val response = DeleteResponseDto(
            success = true,
            message = "Server deleted"
        )

        everySuspend { serverService.delete("server-1") } returns response

        // When: Deleting server
        val result = dataSource.deleteServer("server-1")

        // Then: Result is success
        assertTrue(result.isSuccess)
        assertEquals("Server deleted", result.getOrNull())

        verifySuspend { serverService.delete("server-1") }
    }

    @Test
    fun `deleteServer with success false returns failure`() = runTest {
        // Given: Service returns failure response
        val response = DeleteResponseDto(
            success = false,
            message = "Cannot delete server with clubs"
        )

        everySuspend { serverService.delete("server-1") } returns response

        // When: Deleting server
        val result = dataSource.deleteServer("server-1")

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Cannot delete server with clubs") == true)

        verifySuspend { serverService.delete("server-1") }
    }

    @Test
    fun `getAllServers with empty list returns empty list`() = runTest {
        // Given: Service returns empty servers list
        val dto = ServersResponseDto(servers = emptyList())

        everySuspend { serverService.getAll() } returns dto

        // When: Getting all servers
        val result = dataSource.getAllServers()

        // Then: Result is success with empty list
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)

        verifySuspend { serverService.getAll() }
    }
}
