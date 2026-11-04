package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FolderWithStats
import java.text.NumberFormat
import java.util.Locale

fun getFolderIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "home" -> Icons.Default.Home
        "work" -> Icons.Default.Work
        "person" -> Icons.Default.Person
        "savings" -> Icons.Default.Savings
        "flight" -> Icons.Default.Flight
        "shopping" -> Icons.Default.ShoppingBag
        "car" -> Icons.Default.DirectionsCar
        else -> Icons.Default.Folder
    }
}

@Composable
fun FolderSelectorBar(
    modifier: Modifier = Modifier,
    foldersWithStats: List<FolderWithStats>,
    selectedFolderId: Long?,
    onSelectFolder: (Long?) -> Unit,
    onAddFolderClick: () -> Unit,
    isBalanceHidden: Boolean
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // "Todas las cuentas" item
        item {
            val isAllSelected = selectedFolderId == null
            val borderColor by animateColorAsState(
                targetValue = if (isAllSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                label = "AllBorder"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = if (isAllSelected) 2.dp else 1.dp,
                        color = if (isAllSelected) borderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelectFolder(null) }
                    .testTag("folder_all_chip"),
                shape = RoundedCornerShape(18.dp),
                color = if (isAllSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                tonalElevation = if (isAllSelected) 3.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Todas",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Todas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Global",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Each individual folder card
        items(foldersWithStats, key = { it.folder.id }) { item ->
            val isSelected = selectedFolderId == item.folder.id
            val folderColor = Color(item.folder.colorHex)
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) folderColor else Color.Transparent,
                label = "FolderBorder"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) borderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onSelectFolder(item.folder.id) }
                    .testTag("folder_chip_${item.folder.id}"),
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) folderColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                tonalElevation = if (isSelected) 3.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(folderColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getFolderIcon(item.folder.iconName),
                            contentDescription = item.folder.name,
                            tint = folderColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = item.folder.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isBalanceHidden) "••••" else currencyFormat.format(item.balance),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (item.balance >= 0) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        // "+ Nueva Carpeta" button
        item {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onAddFolderClick() }
                    .testTag("add_folder_button"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva Carpeta",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Nueva",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
