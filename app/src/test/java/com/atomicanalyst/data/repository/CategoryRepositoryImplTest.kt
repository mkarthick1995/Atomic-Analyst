package com.atomicanalyst.data.repository

import com.atomicanalyst.data.db.dao.CategoryDao
import com.atomicanalyst.data.db.entity.CategoryEntity
import com.atomicanalyst.domain.model.Category
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryRepositoryImplTest {
    private class FakeCategoryDao : CategoryDao {
        private val items = mutableMapOf<String, CategoryEntity>()
        private val flow = MutableStateFlow<List<CategoryEntity>>(emptyList())

        override suspend fun upsert(category: CategoryEntity) {
            items[category.id] = category
            emit()
        }

        override suspend fun update(category: CategoryEntity) {
            items[category.id] = category
            emit()
        }

        override suspend fun delete(category: CategoryEntity) {
            items.remove(category.id)
            emit()
        }

        override suspend fun getById(id: String): CategoryEntity? = items[id]

        override fun observeAll(): Flow<List<CategoryEntity>> = flow

        private fun emit() {
            flow.value = items.values.sortedBy { it.name }
        }
    }

    @Test
    fun testUpsertAndObserveAll_ReturnsDomainCategory() = runBlocking {
        val dao = FakeCategoryDao()
        val repository = CategoryRepositoryImpl(dao)
        val category = Category(
            id = "cat-1",
            name = "Food",
            type = TransactionCategory.FOOD,
            isSystem = true,
            createdAtEpochMs = 1700000000000L,
            updatedAtEpochMs = 1700000000000L
        )

        val result = repository.upsert(category)

        assertTrue(result is Result.Success)
        assertEquals(listOf(category), repository.observeAll().first())
        assertEquals(category, (repository.getById("cat-1") as Result.Success).data)
    }
}
