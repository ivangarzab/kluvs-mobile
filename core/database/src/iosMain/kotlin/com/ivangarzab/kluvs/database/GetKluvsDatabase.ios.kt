package com.ivangarzab.kluvs.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS-specific database builder.
 * Uses NSFileManager to get the proper document directory path.
 */
fun getDatabaseBuilder(): RoomDatabase.Builder<KluvsDatabaseImpl> {
    val dbFilePath = documentDirectory() + "/$DATABASE_NAME"
    return Room.databaseBuilder<KluvsDatabaseImpl>(
        name = dbFilePath
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory?.path)
}