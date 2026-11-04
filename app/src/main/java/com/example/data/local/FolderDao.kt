package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders ORDER BY id ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :folderId LIMIT 1")
    fun getFolderById(folderId: Long): Flow<FolderEntity?>

    @Query("SELECT * FROM folders WHERE id = :folderId LIMIT 1")
    suspend fun getFolderByIdOnce(folderId: Long): FolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :folderId")
    suspend fun deleteFolderById(folderId: Long)

    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}
