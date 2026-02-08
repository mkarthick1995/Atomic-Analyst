package com.atomicanalyst.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `standing_instructions` (
                    `id` TEXT NOT NULL,
                    `accountId` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `amountCents` INTEGER NOT NULL,
                    `frequency` TEXT NOT NULL,
                    `nextExecutionEpochMs` INTEGER NOT NULL,
                    `createdAtEpochMs` INTEGER NOT NULL,
                    `updatedAtEpochMs` INTEGER NOT NULL,
                    `isActive` INTEGER NOT NULL,
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_standing_instructions_accountId` " +
                    "ON `standing_instructions` (`accountId`)"
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `account_liability_cross_ref` (
                    `accountId` TEXT NOT NULL,
                    `liabilityAccountId` TEXT NOT NULL,
                    PRIMARY KEY(`accountId`, `liabilityAccountId`),
                    FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`liabilityAccountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_account_liability_cross_ref_accountId` " +
                    "ON `account_liability_cross_ref` (`accountId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_account_liability_cross_ref_liabilityAccountId` " +
                    "ON `account_liability_cross_ref` (`liabilityAccountId`)"
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `transaction_tag_cross_ref` (
                    `transactionId` TEXT NOT NULL,
                    `tagId` TEXT NOT NULL,
                    PRIMARY KEY(`transactionId`, `tagId`),
                    FOREIGN KEY(`transactionId`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`tagId`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_transaction_tag_cross_ref_transactionId` " +
                    "ON `transaction_tag_cross_ref` (`transactionId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_transaction_tag_cross_ref_tagId` " +
                    "ON `transaction_tag_cross_ref` (`tagId`)"
            )
        }
    }

    val MIGRATIONS: Array<Migration> = arrayOf(MIGRATION_1_2)
}
