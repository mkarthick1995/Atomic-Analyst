package com.atomicanalyst.domain.repository

import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun upsert(tag: Tag): Result<Unit>
    suspend fun update(tag: Tag): Result<Unit>
    suspend fun delete(tag: Tag): Result<Unit>
    suspend fun getById(id: String): Result<Tag?>
    fun observeAll(): Flow<List<Tag>>
}
