package com.ivangarzab.kluvs.di

import com.ivangarzab.kluvs.data.local.storage.AndroidSecureStorage
import com.ivangarzab.kluvs.data.local.storage.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * The purpose of this [org.koin.core.Koin] module is to hold all of the platform-specific
 * dependencies for Android.
 */
val androidDataModule = module {
    // SecureStorage - platform-specific implementation
    single<SecureStorage> {
        AndroidSecureStorage(androidContext())
    }
}

actual val platformDataModule: Module = androidDataModule