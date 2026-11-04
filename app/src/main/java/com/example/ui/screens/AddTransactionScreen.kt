package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FolderEntity
import com.example.data.model.Categories
import com.example.ui.components.NumericKeypad
import com.example.ui.components.getFolderIcon
import com.example.ui.theme.FinanceGreen
import com.example.ui.theme.FinanceRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    folders: List<FolderEntity>,
    initialType: String = "EXPENSE",
    initialFolderId: Long? = null,
    onSave: (folderId: Long, amount: Double, type: String, category: String, note: String, dateMillis: Long) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    var transactionType by remember { mutableStateOf(initialType) } // "EXPENSE" or "INCOME"
    val isExpense = transactionType == "EXPENSE"

    // Amount state via string buffer for keypad
    var amountString by remember { mutableStateOf("0") }

    // Selected folder
    var selectedFolderId by remember {
        mutableLongStateOf(initialFolderId ?: folders.firstOrNull()?.id ?: 1L)
    }

    // Categories based on expense vs income
    val categories = if (isExpense) Categories.expenseCategories else Categories.incomeCategories
    var selectedCategory by remember(isExpense) {
        mutableStateOf(categories.first().name)
    }

    // Note / description
    var noteText by remember { mutableStateOf("") }

    // Date
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))

    val parsedAmount = amountString.toDoubleOrNull() ?: 0.0
    val canSave = parsedAmount > 0 && selectedFolderId > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isExpense) "Registrar Gasto" else "Registrar Ingreso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("back_button")) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 1. Transaction Type Selector: Gasto vs Ingreso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Gasto tab
                val isExpenseSelected = transactionType == "EXPENSE"
                val expenseBg by animateColorAsState(
                    targetValue = if (isExpenseSelected) FinanceRed else Color.Transparent,
                    label = "ExpenseBg"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(expenseBg)
                        .clickable {
                            transactionType = "EXPENSE"
                            selectedCategory = Categories.expenseCategories.first().name
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_expense"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gasto (Salida)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpenseSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Ingreso tab
                val isIncomeSelected = transactionType == "INCOME"
                val incomeBg by animateColorAsState(
                    targetValue = if (isIncomeSelected) FinanceGreen else Color.Transparent,
                    label = "IncomeBg"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(incomeBg)
                        .clickable {
                            transactionType = "INCOME"
                            selectedCategory = Categories.incomeCategories.first().name
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_income"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ingreso (Entrada)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isIncomeSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Big Amount Display (Prominent FinTech display)
            val accentColor = if (isExpense) FinanceRed else FinanceGreen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Monto a registrar",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = amountString,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        maxLines = 1,
                        modifier = Modifier.testTag("amount_display")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Selector de Carpeta / Cuenta a la que pertenece
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Carpeta / Cuenta",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    folders.forEach { folder ->
                        val isSelected = selectedFolderId == folder.id
                        val folderColor = Color(folder.colorHex)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) folderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedFolderId = folder.id }
                                .testTag("select_folder_${folder.id}"),
                            color = if (isSelected) folderColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = getFolderIcon(folder.iconName),
                                    contentDescription = folder.name,
                                    tint = folderColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = folder.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Selector de Categoría (Visual Chips / Grid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Categoría",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat.name
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) cat.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategory = cat.name }
                                .testTag("category_${cat.name}"),
                            color = if (isSelected) cat.color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(cat.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = cat.name,
                                        tint = cat.color,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Selector de Fecha y Nota
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date picker button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            calendar.timeInMillis = selectedDateMillis
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, dayOfMonth)
                                    selectedDateMillis = newCal.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .testTag("date_picker_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Fecha",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateFormat.format(Date(selectedDateMillis)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Optional note field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Nota o descripción (opcional)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Nota",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("note_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6. Integrated Numeric Keypad
            NumericKeypad(
                onDigitClick = { digit ->
                    if (digit == ".") {
                        if (!amountString.contains(".")) {
                            amountString += "."
                        }
                    } else {
                        if (amountString == "0") {
                            amountString = digit
                        } else {
                            // Enforce max 2 decimal places
                            val dotIndex = amountString.indexOf(".")
                            if (dotIndex == -1 || amountString.length - dotIndex <= 2) {
                                amountString += digit
                            }
                        }
                    }
                },
                onBackspaceClick = {
                    if (amountString.length > 1) {
                        amountString = amountString.dropLast(1)
                    } else {
                        amountString = "0"
                    }
                },
                onClearClick = {
                    amountString = "0"
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 7. Save Transaction Button
            Button(
                onClick = {
                    if (canSave) {
                        onSave(
                            selectedFolderId,
                            parsedAmount,
                            transactionType,
                            selectedCategory,
                            noteText,
                            selectedDateMillis
                        )
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .height(52.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isExpense) "Guardar Gasto ($ $amountString)" else "Guardar Ingreso ($ $amountString)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
