package com.ivangarzab.kluvs.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ivangarzab.kluvs.database.migrations.MIGRATION_1_2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Platform-agnostic database instantiation function.
 * Takes a platform-specific builder and configures the database with:
 * - BundledSQLiteDriver for cross-platform SQLite support
 * - IO dispatcher for query coroutines
 */
fun getKluvsDatabase(
    builder: RoomDatabase.Builder<KluvsDatabaseImpl>
): KluvsDatabase {
    return builder
        .addMigrations(MIGRATION_1_2)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

internal const val DATABASE_NAME = "kluvs.db"