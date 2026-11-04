package com.example.data.repository

import com.example.data.local.FolderDao
import com.example.data.local.TransactionDao
import com.example.data.local.entities.FolderEntity
import com.example.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ExpenseRepository(
    private val folderDao: FolderDao,
    private val transactionDao: TransactionDao
) {
    val allFolders: Flow<List<FolderEntity>> = folderDao.getAllFolders()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getFolderById(id: Long): Flow<FolderEntity?> = folderDao.getFolderById(id)

    fun getTransactionsByFolder(folderId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByFolder(folderId)

    suspend fun insertFolder(folder: FolderEntity): Long = folderDao.insertFolder(folder)

    suspend fun updateFolder(folder: FolderEntity) = folderDao.updateFolder(folder)

    suspend fun deleteFolder(folder: FolderEntity) = folderDao.deleteFolder(folder)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun seedInitialDataIfNeeded() {
        if (folderDao.getFolderCount() == 0) {
            val personalFolderId = folderDao.insertFolder(
                FolderEntity(
                    name = "Personal",
                    iconName = "person",
                    colorHex = 0xFF2563EB,
                    description = "Gastos diarios y entretenimiento personal"
                )
            )
            val hogarFolderId = folderDao.insertFolder(
                FolderEntity(
                    name = "Hogar",
                    iconName = "home",
                    colorHex = 0xFF10B981,
                    description = "Alquiler, mercado, luz, agua y servicios"
                )
            )
            val oficinaFolderId = folderDao.insertFolder(
                FolderEntity(
                    name = "Oficina",
                    iconName = "work",
                    colorHex = 0xFFF59E0B,
                    description = "Gastos de proyectos, transporte y café"
                )
            )

            val now = System.currentTimeMillis()
            val oneDay = 24 * 60 * 60 * 1000L

            // Seed initial transactions
            // Income
            transactionDao.insertTransaction(
                TransactionEntity(
                    folderId = personalFolderId,
                    amount = 2850.0,
                    type = "INCOME",
                    category = "Salario",
                    note = "Pago de quincena nómina",
                    dateMillis = now - (oneDay * 2)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    folderId = oficinaFolderId,
                    amount = 450.0,
                    type = "INCOME",
                    category = "Ventas",
                    note = "Consultoría proyecto freelance",
                    dateMillis = now - oneDay
                )
            )

            // Expenses
            transactionDao.insertTransaction(
                TransactionEntity(
                    folderId = hogarFolderId,
                    amount = 320.50,
                    type = "EXPENSE",
                    category = "Alimentos",
                    note = "Supermercado compras semanales",
                    dateMillis = now - (oneDay * 1)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    folderId = hogarFolderId,
                    amount = 85.00,
                    type = "EXPENSE",
                    category = "Servicios",
                    note = "Recibo de internet y telefonía",
                    dateMillis = now - (oneDay * 3)
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    folderId = personalFolderId,
                    amount = 45.00,
                    type = "EXPENSE",
                    category = "Transporte",
                    note = "Gasolina y peaje",
                    dateMillis = now
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    folderId = personalFolderId,
                    amount = 65.00,
                    type = "EXPENSE",
                    category = "Ocio",
                    note = "Cena fin de semana con amigos",
                    dateMillis = now
                )
            )
        }
    }
}
