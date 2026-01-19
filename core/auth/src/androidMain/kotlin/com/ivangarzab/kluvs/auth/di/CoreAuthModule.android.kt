package com.ivangarzab.kluvs.auth.di

import com.ivangarzab.kluvs.auth.persistence.AndroidSecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val secureStorageModule = module {
    // SecureStorage - platform-specific implementation
    singleOf(::AndroidSecureStorage)
}