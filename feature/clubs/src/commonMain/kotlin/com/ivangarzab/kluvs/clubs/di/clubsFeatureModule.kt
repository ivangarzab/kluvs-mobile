package com.ivangarzab.kluvs.clubs.di

import com.ivangarzab.kluvs.clubs.domain.GetActiveSessionUseCase
import com.ivangarzab.kluvs.clubs.domain.GetClubDetailsUseCase
import com.ivangarzab.kluvs.clubs.domain.GetMemberClubsUseCase
import com.ivangarzab.kluvs.clubs.presentation.ClubDetailsViewModel
import com.ivangarzab.kluvs.domain.usecases.club.GetClubMembersUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val clubsFeatureModule = module {
    // Use Cases
    factoryOf(::GetActiveSessionUseCase)
    factoryOf(::GetClubDetailsUseCase)
    factoryOf(::GetClubMembersUseCase)
    factoryOf(::GetMemberClubsUseCase)


    // ViewModels
    factoryOf(::ClubDetailsViewModel)
}