package com.atomicanalyst.data.repository

import com.atomicanalyst.data.db.dao.TagDao
import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TagRepositoryImplTest {
    private class FakeTagDao : TagDao {
        private val items = mutableMapOf<String, TagEntity>()
        private val flow = MutableStateFlow<List<TagEntity>>(emptyList())

        override suspend fun upsert(tag: TagEntity) {
            items[tag.id] = tag
            emit()
        }

        override suspend fun update(tag: TagEntity) {
            items[tag.id] = tag
            emit()
        }

        override suspend fun delete(tag: TagEntity) {
            items.remove(tag.id)
            emit()
        }

        override suspend fun getById(id: String): TagEntity? = items[id]

        override fun observeAll(): Flow<List<TagEntity>> = flow

        private fun emit() {
            flow.value = items.values.sortedBy { it.name }
        }
    }

    @Test
    fun testUpsertAndObserveAll_ReturnsDomainTag() = runBlocking {
        val dao = FakeTagDao()
        val repository = TagRepositoryImpl(dao)
        val tag = Tag(
            id = "tag-1",
            name = "Bills",
            createdAtEpochMs = 1700000000000L
        )

        val result = repository.upsert(tag)

        assertTrue(result is Result.Success)
        assertEquals(listOf(tag), repository.observeAll().first())
        assertEquals(tag, (repository.getById("tag-1") as Result.Success).data)
    }
}
