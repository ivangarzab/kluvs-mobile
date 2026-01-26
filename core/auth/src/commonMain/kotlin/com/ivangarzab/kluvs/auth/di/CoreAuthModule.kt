package com.ivangarzab.kluvs.auth.di

import com.ivangarzab.kluvs.auth.domain.AuthRepository
import com.ivangarzab.kluvs.auth.domain.AuthRepositoryImpl
import com.ivangarzab.kluvs.auth.domain.SignOutUseCase
import com.ivangarzab.kluvs.auth.persistence.SecureStorage
import com.ivangarzab.kluvs.auth.remote.AuthService
import com.ivangarzab.kluvs.auth.remote.AuthServiceImpl
import com.ivangarzab.kluvs.database.KluvsDatabase
import io.github.jan.supabase.SupabaseClient
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for authentication dependencies.
 *
 * Provides:
 * - AuthService (Supabase Auth wrapper)
 * - AuthRepository (Auth state management)
 *
 * Note: SecureStorage is provided by platform-specific modules
 * (androidMain/iosMain) since it has platform-specific implementations.
 */
val coreAuthModule = module {

    // AuthService - wraps Supabase Auth
    single<AuthService> {
        AuthServiceImpl(get<SupabaseClient>())
    }

    // AuthRepository - manages auth state and token storage
    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get<AuthService>(),
            secureStorage = get<SecureStorage>() // Provided by platform module
        )
    }

    // Auth UseCases
    factory<SignOutUseCase> {
        SignOutUseCase(
            authRepository = get<AuthRepository>(),
            database = get<KluvsDatabase>()
        )
    }

    // Secure Storage - platform-specific module
    includes(secureStorageModule)
}

expect val secureStorageModule: Module