package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.DatabaseMockFixture
import com.ivangarzab.kluvs.database.entities.ServerEntity
import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.model.Server
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ServerLocalDataSourceTest {

    private lateinit var fixture: DatabaseMockFixture
    private lateinit var dataSource: ServerLocalDataSource

    private fun setup() {
        fixture = DatabaseMockFixture()
        dataSource = ServerLocalDataSourceImpl(fixture.database)
    }

    @Test
    fun `getServer returns server when it exists`() = runTest {
        setup()
        val serverId = "server-1"
        val entity = ServerEntity(serverId, "Test Server", 0)
        everySuspend { fixture.serverDao.getServer(serverId) } returns entity

        val result = dataSource.getServer(serverId)

        assertEquals(entity.toDomain(), result)
    }

    @Test
    fun `getServer returns null when server does not exist`() = runTest {
        setup()
        everySuspend { fixture.serverDao.getServer("not-found") } returns null

        assertNull(dataSource.getServer("not-found"))
    }

    @Test
    fun `getAllServers returns all servers`() = runTest {
        setup()
        val servers = listOf(
            ServerEntity("server-1", "Server 1", 0),
            ServerEntity("server-2", "Server 2", 0)
        )
        everySuspend { fixture.serverDao.getAllServers() } returns servers

        val result = dataSource.getAllServers()

        assertEquals(servers.map { it.toDomain() }, result)
    }

    @Test
    fun `insertServer inserts single server`() = runTest {
        setup()
        val server = Server("server-1", "Test Server", emptyList())
        everySuspend { fixture.serverDao.insertServer(server.toEntity()) } returns Unit

        dataSource.insertServer(server)
    }

    @Test
    fun `deleteServer deletes existing server`() = runTest {
        setup()
        val entity = ServerEntity("server-1", "Test Server", 0)
        everySuspend { fixture.serverDao.getServer("server-1") } returns entity
        everySuspend { fixture.serverDao.deleteServer(entity) } returns Unit

        dataSource.deleteServer("server-1")
    }

    @Test
    fun `deleteAll clears all servers`() = runTest {
        setup()

        dataSource.deleteAll()
    }

    private fun Server.toEntity() = ServerEntity(
        id = id,
        name = name,
        lastFetchedAt = 0
    )
}
