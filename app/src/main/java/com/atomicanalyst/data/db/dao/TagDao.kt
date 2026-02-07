package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.atomicanalyst.data.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tags: List<TagEntity>)

    @Update
    suspend fun update(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("SELECT * FROM tags WHERE id = :id")
    suspend fun getById(id: String): TagEntity?

    @Query("SELECT * FROM tags")
    suspend fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tags ORDER BY name")
    fun observeAll(): Flow<List<TagEntity>>

    @Query("DELETE FROM tags")
    suspend fun clearAll()
}
