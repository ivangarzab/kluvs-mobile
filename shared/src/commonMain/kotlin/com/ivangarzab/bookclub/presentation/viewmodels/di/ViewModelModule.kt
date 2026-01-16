package com.ivangarzab.bookclub.presentation.viewmodels.di

import com.ivangarzab.bookclub.app.AppCoordinator
import com.ivangarzab.bookclub.presentation.viewmodels.auth.AuthViewModel
import com.ivangarzab.bookclub.presentation.viewmodels.club.ClubDetailsViewModel
import com.ivangarzab.bookclub.presentation.viewmodels.member.MeViewModel
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