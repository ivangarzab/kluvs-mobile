package com.ivangarzab.kluvs.domain.usecases.di

import com.ivangarzab.kluvs.domain.usecases.auth.SignOutUseCase
import com.ivangarzab.kluvs.domain.usecases.club.GetActiveSessionUseCase
import com.ivangarzab.kluvs.domain.usecases.club.GetClubDetailsUseCase
import com.ivangarzab.kluvs.domain.usecases.club.GetClubMembersUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetCurrentUserProfileUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetCurrentlyReadingBooksUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetMemberClubsUseCase
import com.ivangarzab.kluvs.domain.usecases.member.GetUserStatisticsUseCase
import com.ivangarzab.kluvs.domain.usecases.util.FormatDateTimeUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for UseCase dependencies.
 *
 * Provides all UseCases as singletons with automatic dependency resolution.
 * UseCases are injected with their required repositories and utility UseCases.
 */
val useCaseModule = module {
    // Utility UseCase
    singleOf(::FormatDateTimeUseCase)

    // Club UseCases
    singleOf(::GetClubDetailsUseCase)
    singleOf(::GetActiveSessionUseCase)
    singleOf(::GetClubMembersUseCase)

    // Member UseCases
    singleOf(::GetCurrentUserProfileUseCase)
    singleOf(::GetUserStatisticsUseCase)
    singleOf(::GetCurrentlyReadingBooksUseCase)
    singleOf(::GetMemberClubsUseCase)

    // Auth UseCases
    singleOf(::SignOutUseCase)
}
