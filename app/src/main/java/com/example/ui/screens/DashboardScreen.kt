package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FolderEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.model.FolderWithStats
import com.example.data.model.PeriodFilter
import com.example.ui.components.BalanceCard
import com.example.ui.components.FolderSelectorBar
import com.example.ui.components.PeriodFilterSelector
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.FinanceGreen
import com.example.ui.theme.FinanceRed
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentBalance: Double,
    currentIncome: Double,
    currentExpense: Double,
    isBalanceHidden: Boolean,
    selectedFolderId: Long?,
    folders: List<FolderEntity>,
    foldersWithStats: List<FolderWithStats>,
    transactions: List<TransactionEntity>,
    periodFilter: PeriodFilter,
    themeMode: ThemeMode,
    onToggleBalanceVisibility: () -> Unit,
    onCycleThemeMode: () -> Unit,
    onSelectFolder: (Long?) -> Unit,
    onPeriodSelected: (PeriodFilter) -> Unit,
    onOpenAddTransaction: (type: String) -> Unit,
    onOpenFoldersManagement: () -> Unit,
    onOpenStats: () -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit
) {
    val selectedFolder = folders.firstOrNull { it.id == selectedFolderId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GastosFlow",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Control Financiero",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Stats / Analytics Button
                    IconButton(
                        onClick = onOpenStats,
                        modifier = Modifier.testTag("nav_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Estadísticas",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Folders Management Button
                    IconButton(
                        onClick = onOpenFoldersManagement,
                        modifier = Modifier.testTag("nav_folders_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Gestionar Carpetas",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Theme Toggle Button (Light / Dark / Auto)
                    IconButton(
                        onClick = onCycleThemeMode,
                        modifier = Modifier.testTag("toggle_theme_button")
                    ) {
                        val (icon, desc) = when (themeMode) {
                            ThemeMode.LIGHT -> Icons.Default.LightMode to "Modo Claro"
                            ThemeMode.DARK -> Icons.Default.DarkMode to "Modo Oscuro"
                            ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness to "Modo Sistema"
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = desc,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Saldo Total Card (with Eye Toggle & Summary of Ingresos / Egresos)
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BalanceCard(
                        balance = currentBalance,
                        totalIncome = currentIncome,
                        totalExpense = currentExpense,
                        isBalanceHidden = isBalanceHidden,
                        selectedFolderName = selectedFolder?.name,
                        periodLabel = periodFilter.label,
                        onToggleVisibility = onToggleBalanceVisibility,
                        onIncomeClick = onOpenStats,
                        onExpenseClick = onOpenStats
                    )
                }
            }

            // 2. Quick Action Buttons: "+ Gasto" & "+ Ingreso"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // "+ Gasto" button
                    Button(
                        onClick = { onOpenAddTransaction("EXPENSE") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_add_expense"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinanceRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Gasto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // "+ Ingreso" button
                    Button(
                        onClick = { onOpenAddTransaction("INCOME") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_add_income"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinanceGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Ingreso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // 3. Independent Folders Carousel / Selector
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Carpetas / Cuentas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Gestionar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .testTag("btn_manage_folders")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    FolderSelectorBar(
                        foldersWithStats = foldersWithStats,
                        selectedFolderId = selectedFolderId,
                        onSelectFolder = onSelectFolder,
                        onAddFolderClick = onOpenFoldersManagement,
                        isBalanceHidden = isBalanceHidden
                    )
                }
            }

            // 4. Period Filter Chips: Diario, Semanal, Mensual, Todo
            item {
                Column {
                    Text(
                        text = "Movimientos (${periodFilter.label})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PeriodFilterSelector(
                        currentFilter = periodFilter,
                        onFilterSelected = onPeriodSelected
                    )
                }
            }

            // 5. Transactions List (Differentiated: Green for Recibido, Red for Pagado)
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No hay movimientos registrados",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Usa los botones superiores para registrar tu primer gasto o ingreso.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(transactions, key = { it.id }) { tx ->
                    val folder = folders.firstOrNull { it.id == tx.folderId }
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TransactionItemCard(
                            transaction = tx,
                            folder = folder,
                            isBalanceHidden = isBalanceHidden,
                            onDeleteClick = { onDeleteTransaction(tx) }
                        )
                    }
                }
            }
        }
    }
}
