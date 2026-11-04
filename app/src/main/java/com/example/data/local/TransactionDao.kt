package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE folderId = :folderId ORDER BY dateMillis DESC, id DESC")
    fun getTransactionsByFolder(folderId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startTime AND dateMillis <= :endTime ORDER BY dateMillis DESC, id DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE folderId = :folderId AND dateMillis >= :startTime AND dateMillis <= :endTime ORDER BY dateMillis DESC, id DESC")
    fun getTransactionsByFolderBetween(folderId: Long, startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getTransactionById(id: Long): Flow<TransactionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions WHERE folderId = :folderId")
    suspend fun deleteTransactionsByFolder(folderId: Long)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int
}
