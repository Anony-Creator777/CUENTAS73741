package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.ExpenseRepository
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FoldersManagementScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.GastosFlowTheme
import com.example.ui.viewmodel.ExpenseViewModel
import com.example.ui.viewmodel.ExpenseViewModelFactory

sealed interface AppScreen {
    data object Dashboard : AppScreen
    data class AddTransaction(val type: String = "EXPENSE", val folderId: Long? = null) : AppScreen
    data object FoldersManagement : AppScreen
    data object Stats : AppScreen
}

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(database.folderDao(), database.transactionDao())
        ExpenseViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            GastosFlowTheme(themeMode = themeMode) {
                MainAppNavigation(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppNavigation(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Dashboard) }

    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()
    val currentIncome by viewModel.currentPeriodIncome.collectAsStateWithLifecycle()
    val currentExpense by viewModel.currentPeriodExpense.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()
    val selectedFolderId by viewModel.selectedFolderId.collectAsStateWithLifecycle()
    val allFolders by viewModel.allFolders.collectAsStateWithLifecycle()
    val foldersWithStats by viewModel.foldersWithStats.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val periodFilter by viewModel.periodFilter.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    Crossfade(
        targetState = currentScreen,
        label = "ScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            is AppScreen.Dashboard -> {
                DashboardScreen(
                    currentBalance = currentBalance,
                    currentIncome = currentIncome,
                    currentExpense = currentExpense,
                    isBalanceHidden = isBalanceHidden,
                    selectedFolderId = selectedFolderId,
                    folders = allFolders,
                    foldersWithStats = foldersWithStats,
                    transactions = filteredTransactions,
                    periodFilter = periodFilter,
                    themeMode = themeMode,
                    onToggleBalanceVisibility = { viewModel.toggleBalanceVisibility() },
                    onCycleThemeMode = { viewModel.cycleThemeMode() },
                    onSelectFolder = { folderId -> viewModel.selectFolder(folderId) },
                    onPeriodSelected = { filter -> viewModel.setPeriodFilter(filter) },
                    onOpenAddTransaction = { type ->
                        currentScreen = AppScreen.AddTransaction(type = type, folderId = selectedFolderId)
                    },
                    onOpenFoldersManagement = {
                        currentScreen = AppScreen.FoldersManagement
                    },
                    onOpenStats = {
                        currentScreen = AppScreen.Stats
                    },
                    onDeleteTransaction = { tx -> viewModel.deleteTransaction(tx) }
                )
            }

            is AppScreen.AddTransaction -> {
                AddTransactionScreen(
                    folders = allFolders,
                    initialType = screen.type,
                    initialFolderId = screen.folderId,
                    onSave = { folderId, amount, type, category, note, dateMillis ->
                        viewModel.addTransaction(
                            folderId = folderId,
                            amount = amount,
                            type = type,
                            category = category,
                            note = note,
                            dateMillis = dateMillis
                        )
                        currentScreen = AppScreen.Dashboard
                    },
                    onDismiss = {
                        currentScreen = AppScreen.Dashboard
                    }
                )
            }

            is AppScreen.FoldersManagement -> {
                FoldersManagementScreen(
                    foldersWithStats = foldersWithStats,
                    onSelectFolder = { folderId ->
                        viewModel.selectFolder(folderId)
                        currentScreen = AppScreen.Dashboard
                    },
                    onCreateFolder = { name, icon, color, desc ->
                        viewModel.createFolder(name, icon, color, desc)
                    },
                    onUpdateFolder = { folder ->
                        viewModel.updateFolder(folder)
                    },
                    onDeleteFolder = { folder ->
                        viewModel.deleteFolder(folder)
                    },
                    onBack = {
                        currentScreen = AppScreen.Dashboard
                    }
                )
            }

            is AppScreen.Stats -> {
                StatsScreen(
                    transactions = filteredTransactions,
                    periodFilter = periodFilter,
                    onPeriodSelected = { filter -> viewModel.setPeriodFilter(filter) },
                    onBack = {
                        currentScreen = AppScreen.Dashboard
                    }
                )
            }
        }
    }
}
