package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FinanceGreen
import com.example.ui.theme.FinanceRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    balance: Double,
    totalIncome: Double,
    totalExpense: Double,
    isBalanceHidden: Boolean,
    selectedFolderName: String?,
    periodLabel: String,
    onToggleVisibility: () -> Unit,
    onIncomeClick: () -> Unit = {},
    onExpenseClick: () -> Unit = {}
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    val formattedBalance = currencyFormat.format(balance)
    val formattedIncome = currencyFormat.format(totalIncome)
    val formattedExpense = currencyFormat.format(totalExpense)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            PrimaryBlue,
            PrimaryBlueDark
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("balance_card"),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row: Folder/Scope tag + Eye toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (selectedFolderName != null) "Carpeta: $selectedFolderName" else "Saldo Total Global",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleVisibility,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_balance_visibility")
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isBalanceHidden) "Mostrar saldo" else "Ocultar saldo",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title label
                Text(
                    text = "Saldo Disponible",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Large Prominent Balance Display
                AnimatedContent(
                    targetState = isBalanceHidden,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "BalanceAnimation"
                ) { hidden ->
                    Text(
                        text = if (hidden) "••••••••" else formattedBalance,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = if (hidden) 3.sp else 0.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Summary Row: Ingresos vs Egresos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ingresos (Recibidos / Verde)
                    SummaryPill(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("summary_income_pill"),
                        label = "Recibido ($periodLabel)",
                        amount = if (isBalanceHidden) "••••" else formattedIncome,
                        icon = Icons.Default.ArrowUpward,
                        iconTint = FinanceGreen,
                        iconBg = Color.White,
                        onClick = onIncomeClick
                    )

                    // Egresos (Pagados / Rojo)
                    SummaryPill(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("summary_expense_pill"),
                        label = "Pagado ($periodLabel)",
                        amount = if (isBalanceHidden) "••••" else formattedExpense,
                        icon = Icons.Default.ArrowDownward,
                        iconTint = FinanceRed,
                        iconBg = Color.White,
                        onClick = onExpenseClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryPill(
    modifier: Modifier = Modifier,
    label: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 1
                )
                Text(
                    text = amount,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}
