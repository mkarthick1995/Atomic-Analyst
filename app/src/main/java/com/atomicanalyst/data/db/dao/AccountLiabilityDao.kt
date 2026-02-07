package com.atomicanalyst.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atomicanalyst.data.db.entity.AccountLiabilityCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountLiabilityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(refs: List<AccountLiabilityCrossRef>)

    @Query("DELETE FROM account_liability_cross_ref WHERE accountId = :accountId")
    suspend fun clearForAccount(accountId: String)

    @Query("SELECT liabilityAccountId FROM account_liability_cross_ref WHERE accountId = :accountId")
    suspend fun getLiabilityIds(accountId: String): List<String>

    @Query("SELECT accountId FROM account_liability_cross_ref WHERE liabilityAccountId = :liabilityAccountId")
    suspend fun getAccountIds(liabilityAccountId: String): List<String>

    @Query("SELECT * FROM account_liability_cross_ref WHERE accountId = :accountId")
    fun observeLiabilityRefs(accountId: String): Flow<List<AccountLiabilityCrossRef>>

    @Query("SELECT * FROM account_liability_cross_ref")
    suspend fun getAll(): List<AccountLiabilityCrossRef>

    @Query("DELETE FROM account_liability_cross_ref")
    suspend fun clearAll()
}
