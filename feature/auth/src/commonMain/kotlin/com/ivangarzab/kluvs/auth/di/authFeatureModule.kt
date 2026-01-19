package com.ivangarzab.kluvs.auth.di

import com.ivangarzab.kluvs.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authFeatureModule = module {

    factoryOf(::AuthViewModel)
}