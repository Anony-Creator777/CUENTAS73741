package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.local.entities.TransactionEntity
import com.example.data.model.Categories
import com.example.data.model.PeriodFilter
import com.example.ui.components.PeriodFilterSelector
import com.example.ui.theme.FinanceGreen
import com.example.ui.theme.FinanceRed
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    transactions: List<TransactionEntity>,
    periodFilter: PeriodFilter,
    onPeriodSelected: (PeriodFilter) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val netSavings = totalIncome - totalExpense

    // Expenses breakdown by category
    val expenseTransactions = transactions.filter { it.type == "EXPENSE" }
    val categoryTotals = expenseTransactions
        .groupBy { it.category }
        .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Estadísticas y Resumen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("stats_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
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
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PeriodFilterSelector(
                    currentFilter = periodFilter,
                    onFilterSelected = onPeriodSelected,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Balance del Período (${periodFilter.label})",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currencyFormat.format(netSavings),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (netSavings >= 0) FinanceGreen else FinanceRed
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Total Ingresos", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    text = currencyFormat.format(totalIncome),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FinanceGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Total Gastos", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    text = currencyFormat.format(totalExpense),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FinanceRed
                                )
                            }
                        }
                    }
                }
            }

            // Category Breakdown Section
            item {
                Text(
                    text = "Gastos por Categoría",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (categoryTotals.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sin gastos registrados en este período",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(categoryTotals) { (categoryName, amount) ->
                    val percentage = if (totalExpense > 0) (amount / totalExpense).toFloat() else 0f
                    val category = Categories.getCategory(categoryName, isExpense = true)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(category.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = categoryName,
                                        tint = category.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = categoryName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = currencyFormat.format(amount),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${(percentage * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { percentage.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = category.color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
