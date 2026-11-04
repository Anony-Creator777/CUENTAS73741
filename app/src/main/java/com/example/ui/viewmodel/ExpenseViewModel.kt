package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.FolderEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.model.FolderWithStats
import com.example.data.model.PeriodFilter
import com.example.data.repository.ExpenseRepository
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _selectedFolderId = MutableStateFlow<Long?>(null)
    val selectedFolderId: StateFlow<Long?> = _selectedFolderId.asStateFlow()

    private val _periodFilter = MutableStateFlow(PeriodFilter.MONTH)
    val periodFilter: StateFlow<PeriodFilter> = _periodFilter.asStateFlow()

    val allFolders: StateFlow<List<FolderEntity>> = repository.allFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Computed folder metrics: each folder with its own balance, income, expense, count
    val foldersWithStats: StateFlow<List<FolderWithStats>> = combine(
        allFolders,
        allTransactions
    ) { folders, transactions ->
        folders.map { folder ->
            val folderTx = transactions.filter { it.folderId == folder.id }
            val income = folderTx.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expense = folderTx.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            FolderWithStats(
                folder = folder,
                balance = income - expense,
                totalIncome = income,
                totalExpense = expense,
                transactionCount = folderTx.size
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered transactions based on selected folder and period filter
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _selectedFolderId,
        _periodFilter
    ) { transactions, folderId, period ->
        val timeRange = period.getTimeRange()
        transactions.filter { tx ->
            val matchesFolder = (folderId == null || tx.folderId == folderId)
            val matchesPeriod = (tx.dateMillis in timeRange.first..timeRange.second)
            matchesFolder && matchesPeriod
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary calculations (for top hero card and summary widgets)
    // Overall balance respects the selected folder, or all folders combined
    val currentBalance: StateFlow<Double> = combine(
        allTransactions,
        _selectedFolderId
    ) { transactions, folderId ->
        val relevantTx = if (folderId != null) {
            transactions.filter { it.folderId == folderId }
        } else {
            transactions
        }
        val income = relevantTx.filter { it.type == "INCOME" }.sumOf { it.amount }
        val expense = relevantTx.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentPeriodIncome: StateFlow<Double> = filteredTransactions.combine(_selectedFolderId) { txList, _ ->
        txList.filter { it.type == "INCOME" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentPeriodExpense: StateFlow<Double> = filteredTransactions.combine(_selectedFolderId) { txList, _ ->
        txList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun toggleBalanceVisibility() {
        _isBalanceHidden.value = !_isBalanceHidden.value
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun cycleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
    }

    fun selectFolder(folderId: Long?) {
        _selectedFolderId.value = folderId
    }

    fun setPeriodFilter(filter: PeriodFilter) {
        _periodFilter.value = filter
    }

    fun addTransaction(
        folderId: Long,
        amount: Double,
        type: String,
        category: String,
        note: String,
        dateMillis: Long
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    folderId = folderId,
                    amount = amount,
                    type = type,
                    category = category,
                    note = note.trim(),
                    dateMillis = dateMillis
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun createFolder(name: String, iconName: String, colorHex: Long, description: String = "") {
        viewModelScope.launch {
            repository.insertFolder(
                FolderEntity(
                    name = name.trim(),
                    iconName = iconName,
                    colorHex = colorHex,
                    description = description.trim()
                )
            )
        }
    }

    fun updateFolder(folder: FolderEntity) {
        viewModelScope.launch {
            repository.updateFolder(folder)
        }
    }

    fun deleteFolder(folder: FolderEntity) {
        viewModelScope.launch {
            if (_selectedFolderId.value == folder.id) {
                _selectedFolderId.value = null
            }
            repository.deleteFolder(folder)
        }
    }
}

class ExpenseViewModelFactory(
    private val repository: ExpenseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
