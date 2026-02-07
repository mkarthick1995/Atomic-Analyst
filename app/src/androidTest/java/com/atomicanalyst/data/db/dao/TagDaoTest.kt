package com.atomicanalyst.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atomicanalyst.data.db.AtomicAnalystDatabase
import com.atomicanalyst.data.db.entity.TagEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TagDaoTest {
    private lateinit var db: AtomicAnalystDatabase
    private lateinit var dao: TagDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AtomicAnalystDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.tagDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun upsertUpdateDelete_WorksAsExpected() = runBlocking {
        val tag = TagEntity(
            id = "tag-1",
            name = "Groceries",
            createdAtEpochMs = 1_000L
        )
        dao.upsert(tag)
        assertEquals(tag, dao.getById("tag-1"))

        val updated = tag.copy(name = "Food")
        dao.update(updated)
        assertEquals("Food", dao.getById("tag-1")?.name)

        dao.delete(updated)
        assertNull(dao.getById("tag-1"))
    }
}
