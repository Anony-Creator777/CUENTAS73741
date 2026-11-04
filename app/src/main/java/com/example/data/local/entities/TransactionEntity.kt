package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["folderId"]), Index(value = ["dateMillis"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val folderId: Long,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String,
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
