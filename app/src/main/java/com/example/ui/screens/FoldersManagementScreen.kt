package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FolderEntity
import com.example.data.model.FolderWithStats
import com.example.ui.components.getFolderIcon
import com.example.ui.theme.FinanceGreen
import com.example.ui.theme.FinanceRed
import com.example.ui.theme.FolderColors
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersManagementScreen(
    foldersWithStats: List<FolderWithStats>,
    onSelectFolder: (Long) -> Unit,
    onCreateFolder: (name: String, iconName: String, colorHex: Long, description: String) -> Unit,
    onUpdateFolder: (FolderEntity) -> Unit,
    onDeleteFolder: (FolderEntity) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var showCreateDialog by remember { mutableStateOf(false) }
    var folderToEdit by remember { mutableStateOf<FolderEntity?>(null) }
    var folderToDelete by remember { mutableStateOf<FolderEntity?>(null) }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Carpetas y Cuentas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("folders_back_button")) {
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("fab_create_folder")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nueva carpeta"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cada carpeta maneja sus propios ingresos, egresos y saldo de forma 100% independiente.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(foldersWithStats, key = { it.folder.id }) { item ->
                val folder = item.folder
                val folderColor = Color(folder.colorHex)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectFolder(folder.id) }
                        .testTag("folder_card_${folder.id}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Header row with Icon, Name, and Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(folderColor.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getFolderIcon(folder.iconName),
                                    contentDescription = folder.name,
                                    tint = folderColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = folder.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (folder.description.isNotBlank()) {
                                    Text(
                                        text = folder.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Edit icon
                            IconButton(
                                onClick = { folderToEdit = folder },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("edit_folder_${folder.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete icon
                            IconButton(
                                onClick = { folderToDelete = folder },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("delete_folder_${folder.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Saldo Actual",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currencyFormat.format(item.balance),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.balance >= 0) MaterialTheme.colorScheme.onSurface else FinanceRed
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Income
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Ingresos",
                                        tint = FinanceGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currencyFormat.format(item.totalIncome),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FinanceGreen
                                    )
                                }

                                // Expense
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Egresos",
                                        tint = FinanceRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currencyFormat.format(item.totalExpense),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FinanceRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${item.transactionCount} movimientos registrados",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Create Folder Dialog
    if (showCreateDialog) {
        FolderFormDialog(
            title = "Nueva Carpeta o Cuenta",
            confirmButtonText = "Crear Carpeta",
            initialName = "",
            initialIcon = "folder",
            initialColor = FolderColors.first(),
            initialDescription = "",
            onConfirm = { name, icon, color, desc ->
                onCreateFolder(name, icon, color, desc)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    // Edit Folder Dialog
    folderToEdit?.let { folder ->
        FolderFormDialog(
            title = "Editar Carpeta",
            confirmButtonText = "Guardar Cambios",
            initialName = folder.name,
            initialIcon = folder.iconName,
            initialColor = folder.colorHex,
            initialDescription = folder.description,
            onConfirm = { name, icon, color, desc ->
                onUpdateFolder(
                    folder.copy(
                        name = name,
                        iconName = icon,
                        colorHex = color,
                        description = desc
                    )
                )
                folderToEdit = null
            },
            onDismiss = { folderToEdit = null }
        )
    }

    // Delete Confirmation Dialog
    folderToDelete?.let { folder ->
        AlertDialog(
            onDismissRequest = { folderToDelete = null },
            title = { Text("¿Eliminar carpeta?") },
            text = {
                Text("Se eliminará la carpeta '${folder.name}' y todos los movimientos asociados a ella de forma permanente.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFolder(folder)
                        folderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { folderToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FolderFormDialog(
    title: String,
    confirmButtonText: String,
    initialName: String,
    initialIcon: String,
    initialColor: Long,
    initialDescription: String,
    onConfirm: (name: String, icon: String, color: Long, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var selectedIcon by remember { mutableStateOf(initialIcon) }
    var selectedColor by remember { mutableLongStateOf(initialColor) }

    val availableIcons = listOf(
        "folder" to "Carpeta",
        "home" to "Hogar",
        "work" to "Oficina",
        "person" to "Personal",
        "savings" to "Ahorros",
        "shopping" to "Compras",
        "car" to "Auto",
        "flight" to "Viajes"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (ej. Hogar, Oficina)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("folder_name_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("folder_desc_input")
                )

                Text(
                    text = "Color de la carpeta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FolderColors) { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Icono representativo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableIcons) { (iconKey, _) ->
                        val isSelected = selectedIcon == iconKey
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(selectedColor) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedIcon = iconKey },
                            color = if (isSelected) Color(selectedColor).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = getFolderIcon(iconKey),
                                    contentDescription = iconKey,
                                    tint = if (isSelected) Color(selectedColor) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedIcon, selectedColor, description)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("confirm_folder_button")
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
