package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.CategoryDao
import com.atomicanalyst.data.db.mapper.toDomain
import com.atomicanalyst.data.db.mapper.toEntity
import com.atomicanalyst.domain.model.Category
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : BaseRepository(), CategoryRepository {
    override suspend fun upsert(category: Category): Result<Unit> = safeCall {
        categoryDao.upsert(category.toEntity())
    }

    override suspend fun update(category: Category): Result<Unit> = safeCall {
        categoryDao.update(category.toEntity())
    }

    override suspend fun delete(category: Category): Result<Unit> = safeCall {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getById(id: String): Result<Category?> = safeCall {
        categoryDao.getById(id)?.toDomain()
    }

    override fun observeAll(): Flow<List<Category>> = categoryDao.observeAll()
        .map { categories -> categories.map { it.toDomain() } }
}
