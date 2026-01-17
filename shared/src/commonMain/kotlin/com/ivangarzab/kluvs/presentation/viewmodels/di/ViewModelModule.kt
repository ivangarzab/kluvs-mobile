package com.ivangarzab.kluvs.presentation.viewmodels.di

import com.ivangarzab.kluvs.app.AppCoordinator
import com.ivangarzab.kluvs.presentation.viewmodels.auth.AuthViewModel
import com.ivangarzab.kluvs.presentation.viewmodels.club.ClubDetailsViewModel
import com.ivangarzab.kluvs.presentation.viewmodels.member.MeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
    // Cross-platform navigation ViewModel
    // Singleton - survives navigation
    singleOf(::AppCoordinator)

    factoryOf(::AuthViewModel)
    factoryOf(::ClubDetailsViewModel)
    factoryOf(::MeViewModel)
}