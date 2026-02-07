package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.atomicanalyst.data.db.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(accounts: List<AccountEntity>)

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts ORDER BY name")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("DELETE FROM accounts")
    suspend fun clearAll()
}
