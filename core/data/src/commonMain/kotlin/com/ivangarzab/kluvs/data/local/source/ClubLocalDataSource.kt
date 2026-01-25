package com.ivangarzab.kluvs.data.local.source

import com.ivangarzab.kluvs.data.local.mappers.toDomain
import com.ivangarzab.kluvs.data.local.mappers.toEntity
import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.model.Club

/**
 * Local data source for Club entities.
 * Handles CRUD operations with the local Room database.
 */
interface ClubLocalDataSource {
    suspend fun getClub(clubId: String): Club?
    suspend fun getClubsForServer(serverId: String): List<Club>
    suspend fun insertClub(club: Club)
    suspend fun insertClubs(clubs: List<Club>)
    suspend fun deleteClub(clubId: String)
    suspend fun getLastFetchedAt(clubId: String): Long?
    suspend fun deleteAll()
}

/**
 * Implementation of [ClubLocalDataSource] using Room database.
 */
class ClubLocalDataSourceImpl(
    private val database: KluvsDatabase
) : ClubLocalDataSource {

    private val clubDao = database.clubDao()

    override suspend fun getClub(clubId: String): Club? {
        return clubDao.getClub(clubId)?.toDomain()
    }

    override suspend fun getClubsForServer(serverId: String): List<Club> {
        return clubDao.getClubsForServer(serverId).map { it.toDomain() }
    }

    override suspend fun insertClub(club: Club) {
        clubDao.insertClub(club.toEntity())
    }

    override suspend fun insertClubs(clubs: List<Club>) {
        clubDao.insertClubs(clubs.map { it.toEntity() })
    }

    override suspend fun deleteClub(clubId: String) {
        val entity = clubDao.getClub(clubId)
        if (entity != null) {
            clubDao.deleteClub(entity)
        }
    }

    override suspend fun getLastFetchedAt(clubId: String): Long? {
        return clubDao.getLastFetchedAt(clubId)
    }

    override suspend fun deleteAll() {
        clubDao.deleteAll()
    }
}
