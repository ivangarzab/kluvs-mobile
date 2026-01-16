package com.ivangarzab.bookclub.data.remote.di

import com.ivangarzab.bookclub.data.remote.api.ClubService
import com.ivangarzab.bookclub.data.remote.api.ClubServiceImpl
import com.ivangarzab.bookclub.data.remote.api.MemberService
import com.ivangarzab.bookclub.data.remote.api.MemberServiceImpl
import com.ivangarzab.bookclub.data.remote.api.ServerService
import com.ivangarzab.bookclub.data.remote.api.ServerServiceImpl
import com.ivangarzab.bookclub.data.remote.api.SessionService
import com.ivangarzab.bookclub.data.remote.api.SessionServiceImpl
import com.ivangarzab.bookclub.data.remote.source.ClubRemoteDataSource
import com.ivangarzab.bookclub.data.remote.source.ClubRemoteDataSourceImpl
import com.ivangarzab.bookclub.data.remote.source.MemberRemoteDataSource
import com.ivangarzab.bookclub.data.remote.source.MemberRemoteDataSourceImpl
import com.ivangarzab.bookclub.data.remote.source.ServerRemoteDataSource
import com.ivangarzab.bookclub.data.remote.source.ServerRemoteDataSourceImpl
import com.ivangarzab.bookclub.data.remote.source.SessionRemoteDataSource
import com.ivangarzab.bookclub.data.remote.source.SessionRemoteDataSourceImpl
import com.ivangarzab.bookclub.shared.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import org.koin.dsl.module

/**
 * [org.koin.core.Koin] module for the data layer.
 */
val remoteDataModule = module {

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_KEY
        ) {
            install(Functions)
            install(Auth)
        }
    }

    // Services
    single<ClubService> { ClubServiceImpl(get<SupabaseClient>()) }
    single<MemberService> { MemberServiceImpl(get<SupabaseClient>()) }
    single<ServerService> { ServerServiceImpl(get<SupabaseClient>()) }
    single<SessionService> { SessionServiceImpl(get<SupabaseClient>()) }

    // Remote Data Sources
    single<ClubRemoteDataSource> { ClubRemoteDataSourceImpl(get<ClubService>()) }
    single<MemberRemoteDataSource> { MemberRemoteDataSourceImpl(get<MemberService>()) }
    single<ServerRemoteDataSource> { ServerRemoteDataSourceImpl(get<ServerService>()) }
    single<SessionRemoteDataSource> { SessionRemoteDataSourceImpl(get<SessionService>()) }
}