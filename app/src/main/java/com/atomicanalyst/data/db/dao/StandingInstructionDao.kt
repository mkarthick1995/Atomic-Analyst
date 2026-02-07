package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.atomicanalyst.data.db.entity.StandingInstructionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StandingInstructionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(instruction: StandingInstructionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(instructions: List<StandingInstructionEntity>)

    @Update
    suspend fun update(instruction: StandingInstructionEntity)

    @Delete
    suspend fun delete(instruction: StandingInstructionEntity)

    @Query("SELECT * FROM standing_instructions WHERE id = :id")
    suspend fun getById(id: String): StandingInstructionEntity?

    @Query("SELECT * FROM standing_instructions WHERE accountId = :accountId")
    suspend fun getByAccountId(accountId: String): List<StandingInstructionEntity>

    @Query("SELECT * FROM standing_instructions WHERE accountId = :accountId")
    fun observeByAccountId(accountId: String): Flow<List<StandingInstructionEntity>>

    @Query("SELECT * FROM standing_instructions")
    suspend fun getAll(): List<StandingInstructionEntity>

    @Query("DELETE FROM standing_instructions")
    suspend fun clearAll()
}
