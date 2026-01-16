package com.ivangarzab.bookclub.data.auth.di

import com.ivangarzab.bookclub.data.auth.AuthRepository
import com.ivangarzab.bookclub.data.auth.AuthRepositoryImpl
import com.ivangarzab.bookclub.data.auth.AuthService
import com.ivangarzab.bookclub.data.auth.AuthServiceImpl
import io.github.jan.supabase.SupabaseClient
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
val authModule = module {

    // AuthService - wraps Supabase Auth
    single<AuthService> {
        AuthServiceImpl(get<SupabaseClient>())
    }

    // AuthRepository - manages auth state and token storage
    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get<AuthService>(),
            secureStorage = get() // Provided by platform module
        )
    }
}