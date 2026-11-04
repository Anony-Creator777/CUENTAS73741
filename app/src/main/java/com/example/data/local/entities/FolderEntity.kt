package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "folder",
    val colorHex: Long = 0xFF2563EB,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
