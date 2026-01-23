package com.ivangarzab.kluvs.member.di

import com.ivangarzab.kluvs.member.domain.GetCurrentUserProfileUseCase
import com.ivangarzab.kluvs.member.domain.GetCurrentlyReadingBooksUseCase
import com.ivangarzab.kluvs.member.domain.GetUserStatisticsUseCase
import com.ivangarzab.kluvs.member.domain.UpdateAvatarUseCase
import com.ivangarzab.kluvs.member.presentation.MeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val memberFeatureModule = module {
    // UseCases
    factoryOf(::GetCurrentlyReadingBooksUseCase)
    factoryOf(::GetCurrentUserProfileUseCase)
    factoryOf(::GetUserStatisticsUseCase)
    factoryOf(::UpdateAvatarUseCase)

    // ViewModels
    factoryOf(::MeViewModel)
}