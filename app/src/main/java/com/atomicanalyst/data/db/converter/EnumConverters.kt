package com.atomicanalyst.data.db.converter

import androidx.room.TypeConverter
import com.atomicanalyst.domain.model.AccountType
import com.atomicanalyst.domain.model.Frequency
import com.atomicanalyst.domain.model.ReconciliationAction
import com.atomicanalyst.domain.model.TransactionCategory
import com.atomicanalyst.domain.model.TransactionSource

class EnumConverters {
    @TypeConverter
    fun transactionSourceToString(value: TransactionSource?): String? = value?.name

    @TypeConverter
    fun stringToTransactionSource(value: String?): TransactionSource? =
        value?.let { enumValueOf<TransactionSource>(it) }

    @TypeConverter
    fun accountTypeToString(value: AccountType?): String? = value?.name

    @TypeConverter
    fun stringToAccountType(value: String?): AccountType? = value?.let { enumValueOf<AccountType>(it) }

    @TypeConverter
    fun transactionCategoryToString(value: TransactionCategory?): String? = value?.name

    @TypeConverter
    fun stringToTransactionCategory(value: String?): TransactionCategory? =
        value?.let { enumValueOf<TransactionCategory>(it) }

    @TypeConverter
    fun reconciliationActionToString(value: ReconciliationAction?): String? = value?.name

    @TypeConverter
    fun stringToReconciliationAction(value: String?): ReconciliationAction? =
        value?.let { enumValueOf<ReconciliationAction>(it) }

    @TypeConverter
    fun frequencyToString(value: Frequency?): String? = value?.name

    @TypeConverter
    fun stringToFrequency(value: String?): Frequency? =
        value?.let { enumValueOf<Frequency>(it) }
}
