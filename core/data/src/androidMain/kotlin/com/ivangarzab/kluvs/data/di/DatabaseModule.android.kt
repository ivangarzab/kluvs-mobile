package com.ivangarzab.kluvs.data.di

import com.ivangarzab.kluvs.database.KluvsDatabase
import com.ivangarzab.kluvs.database.getDatabaseBuilder
import com.ivangarzab.kluvs.database.getKluvsDatabase
import org.koin.dsl.module

actual val databaseModule = module {
    single<KluvsDatabase> {
        getKluvsDatabase(getDatabaseBuilder(get()))
    }
}
