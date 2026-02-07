package com.atomicanalyst.data.repository

import com.atomicanalyst.data.BaseRepository
import com.atomicanalyst.data.db.dao.TagDao
import com.atomicanalyst.data.db.mapper.toDomain
import com.atomicanalyst.data.db.mapper.toEntity
import com.atomicanalyst.domain.model.Result
import com.atomicanalyst.domain.model.Tag
import com.atomicanalyst.domain.repository.TagRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : BaseRepository(), TagRepository {
    override suspend fun upsert(tag: Tag): Result<Unit> = safeCall {
        tagDao.upsert(tag.toEntity())
    }

    override suspend fun update(tag: Tag): Result<Unit> = safeCall {
        tagDao.update(tag.toEntity())
    }

    override suspend fun delete(tag: Tag): Result<Unit> = safeCall {
        tagDao.delete(tag.toEntity())
    }

    override suspend fun getById(id: String): Result<Tag?> = safeCall {
        tagDao.getById(id)?.toDomain()
    }

    override fun observeAll(): Flow<List<Tag>> = tagDao.observeAll()
        .map { tags -> tags.map { it.toDomain() } }
}
