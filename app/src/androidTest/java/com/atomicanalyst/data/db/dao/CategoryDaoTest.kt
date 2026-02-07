package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.domain.model.TransactionCategory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: CategoryDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.categoryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertAndDelete_SupportsNullableType() = runBlocking {
        val category = CategoryEntity(
            id = "cat-1",
            name = "Misc",
            type = null,
            isSystem = false,
            createdAtEpochMs = 1_000L,
            updatedAtEpochMs = 2_000L
        )
        dao.upsert(category)
        assertEquals(category, dao.getById("cat-1"))

        val updated = category.copy(type = TransactionCategory.FOOD)
        dao.update(updated)
        assertEquals(TransactionCategory.FOOD, dao.getById("cat-1")?.type)

        dao.delete(updated)
        assertNull(dao.getById("cat-1"))
    }
}
