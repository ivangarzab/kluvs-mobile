package com.ivangarzab.kluvs.data.di

import com.ivangarzab.kluvs.data.remote.api.AvatarService
import com.ivangarzab.kluvs.data.remote.api.AvatarServiceImpl
import com.ivangarzab.kluvs.data.remote.api.ClubService
import com.ivangarzab.kluvs.data.remote.api.ClubServiceImpl
import com.ivangarzab.kluvs.data.remote.api.MemberService
import com.ivangarzab.kluvs.data.remote.api.MemberServiceImpl
import com.ivangarzab.kluvs.data.remote.api.ServerService
import com.ivangarzab.kluvs.data.remote.api.ServerServiceImpl
import com.ivangarzab.kluvs.data.remote.api.SessionService
import com.ivangarzab.kluvs.data.remote.api.SessionServiceImpl
import com.ivangarzab.kluvs.data.remote.source.AvatarRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.AvatarRemoteDataSourceImpl
import com.ivangarzab.kluvs.data.remote.source.ClubRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.ClubRemoteDataSourceImpl
import com.ivangarzab.kluvs.data.remote.source.MemberRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.MemberRemoteDataSourceImpl
import com.ivangarzab.kluvs.data.remote.source.ServerRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.ServerRemoteDataSourceImpl
import com.ivangarzab.kluvs.data.remote.source.SessionRemoteDataSource
import com.ivangarzab.kluvs.data.remote.source.SessionRemoteDataSourceImpl
import com.ivangarzab.kluvs.data.repositories.AvatarRepository
import com.ivangarzab.kluvs.data.repositories.AvatarRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.ClubRepository
import com.ivangarzab.kluvs.data.repositories.ClubRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.MemberRepository
import com.ivangarzab.kluvs.data.repositories.MemberRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.ServerRepository
import com.ivangarzab.kluvs.data.repositories.ServerRepositoryImpl
import com.ivangarzab.kluvs.data.repositories.SessionRepository
import com.ivangarzab.kluvs.data.repositories.SessionRepositoryImpl
import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module

val coreDataModule = module {
    // Services
    single<ClubService> { ClubServiceImpl(get<SupabaseClient>()) }
    single<MemberService> { MemberServiceImpl(get<SupabaseClient>()) }
    single<ServerService> { ServerServiceImpl(get<SupabaseClient>()) }
    single<SessionService> { SessionServiceImpl(get<SupabaseClient>()) }
    single<AvatarService> { AvatarServiceImpl(get<SupabaseClient>()) }

    // Remote Data Sources
    single<AvatarRemoteDataSource> { AvatarRemoteDataSourceImpl(get<AvatarService>()) }
    single<ClubRemoteDataSource> { ClubRemoteDataSourceImpl(get<ClubService>()) }
    single<MemberRemoteDataSource> { MemberRemoteDataSourceImpl(get<MemberService>()) }
    single<ServerRemoteDataSource> { ServerRemoteDataSourceImpl(get<ServerService>()) }
    single<SessionRemoteDataSource> { SessionRemoteDataSourceImpl(get<SessionService>()) }

    // Repositories
    single<AvatarRepository> { AvatarRepositoryImpl(get<AvatarRemoteDataSource>()) }
    single<ServerRepository> { ServerRepositoryImpl(get<ServerRemoteDataSource>()) }
    single<ClubRepository> { ClubRepositoryImpl(get<ClubRemoteDataSource>()) }
    single<MemberRepository> { MemberRepositoryImpl(get<MemberRemoteDataSource>()) }
    single<SessionRepository> { SessionRepositoryImpl(get<SessionRemoteDataSource>()) }
}