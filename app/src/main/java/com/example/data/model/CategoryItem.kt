package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object Categories {
    val expenseCategories = listOf(
        CategoryItem("Alimentos", Icons.Default.Restaurant, Color(0xFFF97316)),
        CategoryItem("Transporte", Icons.Default.DirectionsCar, Color(0xFF3B82F6)),
        CategoryItem("Hogar", Icons.Default.Home, Color(0xFF6366F1)),
        CategoryItem("Servicios", Icons.Default.FlashOn, Color(0xFFF59E0B)),
        CategoryItem("Salud", Icons.Default.LocalHospital, Color(0xFFEF4444)),
        CategoryItem("Ocio", Icons.Default.Movie, Color(0xFF8B5CF6)),
        CategoryItem("Educación", Icons.Default.School, Color(0xFF14B8A6)),
        CategoryItem("Compras", Icons.Default.ShoppingBag, Color(0xFFEC4899)),
        CategoryItem("Viajes", Icons.Default.Flight, Color(0xFF0EA5E9)),
        CategoryItem("Otros", Icons.Default.Category, Color(0xFF64748B))
    )

    val incomeCategories = listOf(
        CategoryItem("Salario", Icons.Default.Payments, Color(0xFF10B981)),
        CategoryItem("Ventas", Icons.Default.Storefront, Color(0xFF059669)),
        CategoryItem("Inversiones", Icons.Default.TrendingUp, Color(0xFF0D9488)),
        CategoryItem("Reembolsos", Icons.Default.Autorenew, Color(0xFF0284C7)),
        CategoryItem("Premios / Regalos", Icons.Default.CardGiftcard, Color(0xFFD97706)),
        CategoryItem("Otros Ingresos", Icons.Default.AccountBalance, Color(0xFF4F46E5))
    )

    fun getCategory(name: String, isExpense: Boolean = true): CategoryItem {
        val list = if (isExpense) expenseCategories else incomeCategories
        return list.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: (expenseCategories + incomeCategories).firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: CategoryItem(name, Icons.Default.Category, Color(0xFF64748B))
    }
}
