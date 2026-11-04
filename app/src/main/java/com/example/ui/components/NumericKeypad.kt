package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumericKeypad(
    modifier: Modifier = Modifier,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit = {}
) {
    val keyRows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "BACKSPACE")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        keyRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp),
                        onClick = {
                            if (key == "BACKSPACE") {
                                onBackspaceClick()
                            } else {
                                onDigitClick(key)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val contentColor = MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        modifier = modifier.testTag("keypad_$key"),
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (key == "BACKSPACE") {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Borrar dígito",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = key,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    }
}
