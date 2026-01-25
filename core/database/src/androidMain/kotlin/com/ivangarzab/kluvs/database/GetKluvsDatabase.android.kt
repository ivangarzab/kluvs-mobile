package com.ivangarzab.kluvs.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android-specific database builder.
 * Requires Android context to determine database file path.
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<KluvsDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<KluvsDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}