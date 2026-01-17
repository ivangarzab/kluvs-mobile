package com.ivangarzab.kluvs.di

import com.ivangarzab.kluvs.data.local.storage.IosSecureStorage
import com.ivangarzab.kluvs.data.local.storage.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * The purpose of this [org.koin.core.Koin] module is to hold all of the platform-specific
 * dependencies for iOS.
 */
val iosDataModule = module {
    // SecureStorage - platform-specific implementation
    single<SecureStorage> { IosSecureStorage() }
}

actual val platformDataModule: Module = iosDataModule