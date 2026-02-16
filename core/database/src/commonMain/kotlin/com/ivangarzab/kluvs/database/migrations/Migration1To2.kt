package com.ivangarzab.kluvs.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Migration from database version 1 to 2.
 *
 * Changes:
 * - Remove `points` column from `members` table (API v0.7.0: points field removed)
 * - Add `imageUrl` column to `books` table (API v0.7.0: new book image field)
 * - Add `externalGoogleId` column to `books` table (API v0.7.0: Google Books integration)
 *
 * SQLite does not support DROP COLUMN directly in older versions, so `members` is
 * recreated without the `points` column.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // Recreate members table without points column
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `members_new` (
                `id` TEXT NOT NULL,
                `userId` TEXT,
                `name` TEXT,
                `handle` TEXT,
                `avatarPath` TEXT,
                `booksRead` INTEGER NOT NULL,
                `role` TEXT,
                `createdAt` TEXT,
                `lastFetchedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT INTO `members_new` (id, userId, name, handle, avatarPath, booksRead, role, createdAt, lastFetchedAt)
            SELECT id, userId, name, handle, avatarPath, booksRead, role, createdAt, lastFetchedAt
            FROM `members`
            """.trimIndent()
        )
        connection.execSQL("DROP TABLE `members`")
        connection.execSQL("ALTER TABLE `members_new` RENAME TO `members`")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_members_userId` ON `members` (`userId`)")

        // Add new columns to books table
        connection.execSQL("ALTER TABLE `books` ADD COLUMN `imageUrl` TEXT")
        connection.execSQL("ALTER TABLE `books` ADD COLUMN `externalGoogleId` TEXT")
    }
}
