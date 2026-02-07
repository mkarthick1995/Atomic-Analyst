package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.Category
import com.atomicanalyst.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun upsert(category: Category): Result<Unit>
    suspend fun update(category: Category): Result<Unit>
    suspend fun delete(category: Category): Result<Unit>
    suspend fun getById(id: String): Result<Category?>
    fun observeAll(): Flow<List<Category>>
}
