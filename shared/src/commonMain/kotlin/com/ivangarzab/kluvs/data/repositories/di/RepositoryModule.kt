package com.ivangarzab.kluvs.data.repositories.di

import com.ivangarzab.kluvs.data.remote.source.ClubRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.MemberRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.ServerRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.SessionRemoteDataSource
import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.data.repositories.ClubRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.ServerRepository
import com.ivangarzab.kluvs.data.repositories.ServerRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.SessionRepository
import com.ivangarzab.kluvs.data.repositories.SessionRepositoryImpl
import org.koin.dsl.module

/**
 * [org.koin.core.Koin] module for the repository layer.
 *
 * This module provides repository instances that abstract data access.
 * Currently, repositories delegate to remote data sources, but can be extended
 * to include local data sources for caching and offline support.
 */
val repositoryModule = module {

    // Repositories
    single<ServerRepository> { ServerRepositoryImpl(get<ServerRemoteDataSource>()) }
    single<ClubRepository> { ClubRepositoryImpl(get<ClubRemoteDataSource>()) }
    single<MemberRepository> { MemberRepositoryImpl(get<MemberRemoteDataSource>()) }
    single<SessionRepository> { SessionRepositoryImpl(get<SessionRemoteDataSource>()) }
}
