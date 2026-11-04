package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FolderEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.model.Categories
import com.example.data.model.PeriodFilter
import com.example.ui.theme.FinanceGreen
import com.example.ui.theme.FinanceRed
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PeriodFilterSelector(
    modifier: Modifier = Modifier,
    currentFilter: PeriodFilter,
    onFilterSelected: (PeriodFilter) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PeriodFilter.entries.forEach { filter ->
            val isSelected = filter == currentFilter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
            )
        }
    }
}

@Composable
fun TransactionItemCard(
    modifier: Modifier = Modifier,
    transaction: TransactionEntity,
    folder: FolderEntity?,
    isBalanceHidden: Boolean,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit = {}
) {
    val isExpense = transaction.type == "EXPENSE"
    val categoryItem = Categories.getCategory(transaction.category, isExpense)

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    val formattedAmount = if (isBalanceHidden) {
        "••••"
    } else {
        val sign = if (isExpense) "-" else "+"
        "$sign ${currencyFormat.format(transaction.amount)}"
    }

    val amountColor = if (isExpense) FinanceRed else FinanceGreen
    val amountBg = if (isExpense) FinanceRed.copy(alpha = 0.1f) else FinanceGreen.copy(alpha = 0.1f)

    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("es", "ES"))
    val formattedDate = dateFormat.format(Date(transaction.dateMillis))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("transaction_item_${transaction.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with colored background
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(categoryItem.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryItem.icon,
                    contentDescription = transaction.category,
                    tint = categoryItem.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column: Category, Folder tag & Note, Date
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (folder != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(folder.colorHex).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = folder.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(folder.colorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (transaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Amount pill
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = amountBg,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = formattedAmount,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(top = 2.dp)
                        .testTag("delete_transaction_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar transacción",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
