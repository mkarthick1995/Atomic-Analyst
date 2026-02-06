package com.atomicanalyst.di

import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseModuleTest {
    @Test
    fun testDatabaseName_isStable() {
        assertEquals("atomic_analyst.db", DatabaseModule.provideDatabaseName())
    }
}
