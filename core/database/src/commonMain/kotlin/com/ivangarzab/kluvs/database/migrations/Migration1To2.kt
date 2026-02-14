package com.ivangarzab.kluvs.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    override fun migrate(db: SupportSQLiteDatabase) {
        // Recreate members table without points column
        db.execSQL(
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
        db.execSQL(
            """
            INSERT INTO `members_new` (id, userId, name, handle, avatarPath, booksRead, role, createdAt, lastFetchedAt)
            SELECT id, userId, name, handle, avatarPath, booksRead, role, createdAt, lastFetchedAt
            FROM `members`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `members`")
        db.execSQL("ALTER TABLE `members_new` RENAME TO `members`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_members_userId` ON `members` (`userId`)")

        // Add new columns to books table
        db.execSQL("ALTER TABLE `books` ADD COLUMN `imageUrl` TEXT")
        db.execSQL("ALTER TABLE `books` ADD COLUMN `externalGoogleId` TEXT")
    }
}
