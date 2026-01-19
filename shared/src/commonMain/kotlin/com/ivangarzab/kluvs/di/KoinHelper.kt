package com.ivangarzab.kluvs.di

import com.ivangarzab.kluvs.auth.di.authModule
import com.ivangarzab.kluvs.data.remote.di.remoteDataModule
import com.ivangarzab.kluvs.data.repositories.di.repositoryModule
import com.ivangarzab.kluvs.domain.usecases.di.useCaseModule
import com.ivangarzab.kluvs.network.di.coreNetworkModule
import com.ivangarzab.kluvs.presentation.viewmodels.di.viewModelModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * The purpose of this expected [org.koin.core.Koin] module is to hold all of the platform-specific dependencies.
 */
expect val platformDataModule: Module

/**
 * The purpose of this function is to initialize [org.koin.core.Koin] and all of its relevant modules.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        generalModule,
        platformDataModule,
        coreNetworkModule,
        remoteDataModule,
        authModule,
        repositoryModule,
        useCaseModule,
        viewModelModule,
    )
}

fun initKoin() = initKoin {}

private val generalModule = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob())
    }
}