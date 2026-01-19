package com.ivangarzab.kluvs.auth.di

import com.ivangarzab.kluvs.auth.persistence.AndroidSecureStorage
import com.ivangarzab.kluvs.auth.persistence.SecureStorage
import org.koin.dsl.module

actual val secureStorageModule = module {
    // SecureStorage - platform-specific implementation
    single<SecureStorage> { AndroidSecureStorage(get()) }
}