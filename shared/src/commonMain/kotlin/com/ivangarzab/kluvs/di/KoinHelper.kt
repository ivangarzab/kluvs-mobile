package com.ivangarzab.kluvs.di

import com.ivangarzab.kluvs.app.AppCoordinator
import com.ivangarzab.kluvs.auth.di.authFeatureModule
import com.ivangarzab.kluvs.auth.di.coreAuthModule
import com.ivangarzab.kluvs.clubs.di.clubsFeatureModule
import com.ivangarzab.kluvs.data.di.coreDataModule
import com.ivangarzab.kluvs.member.di.memberFeatureModule
import com.ivangarzab.kluvs.network.di.coreNetworkModule
import com.ivangarzab.kluvs.presentation.di.corePresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
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
        // core modules
        coreNetworkModule,
        coreDataModule,
        corePresentationModule,
        coreAuthModule,
        //feature modules
        authFeatureModule,
        clubsFeatureModule,
        memberFeatureModule
    )
}

fun initKoin() = initKoin {}

private val generalModule = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob())
    }
    // Cross-platform navigation ViewModel
    // Singleton - survives navigation
    singleOf(::AppCoordinator)
}