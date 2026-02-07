package com.atomicanalyst.data.db

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AtomicAnalystDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AtomicAnalystDatabase::class.java,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate_1_to_1_schemaIsValid() {
        val db = helper.createDatabase(TEST_DB, 1)
        db.close()

        helper.runMigrationsAndValidate(TEST_DB, 1, true)
    }

    private companion object {
        private const val TEST_DB = "atomic-analyst-migration-test"
    }
}
