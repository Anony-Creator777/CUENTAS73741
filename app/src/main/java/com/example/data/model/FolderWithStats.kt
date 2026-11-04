package com.example.data.model

import com.example.data.local.entities.FolderEntity

data class FolderWithStats(
    val folder: FolderEntity,
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactionCount: Int = 0
)
