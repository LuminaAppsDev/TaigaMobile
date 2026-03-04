package com.luminaapps.taigamobile.ui.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.ui.utils.clickableUnindicated

/**
 * Color picker with material dialog
 */

@Composable
fun ColorPicker(
    size: Dp,
    color: Color,
    onColorPicked: (Color) -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedColor by remember(color) { mutableStateOf(color) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.select_color)) },
            text = {
                val colors = (listOf(color) + PrimaryColorPalette).toSet().toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(colors) { c ->
                        val isSelected = c == selectedColor
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(c, CircleShape)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedColor = c },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    Modifier
                                        .size(20.dp)
                                        .background(MaterialTheme.colorScheme.onSurface, CircleShape)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onColorPicked(selectedColor)
                    showDialog = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Spacer(
        Modifier.size(size)
            .background(color = color, shape = MaterialTheme.shapes.small)
            .clickableUnindicated { showDialog = true }
    )
}

/**
 * Material Design primary color palette, replacing ColorPalette.Primary from vanpra library
 */
val PrimaryColorPalette = listOf(
    Color(0xFFF44336), // Red
    Color(0xFFE91E63), // Pink
    Color(0xFF9C27B0), // Purple
    Color(0xFF673AB7), // Deep Purple
    Color(0xFF3F51B5), // Indigo
    Color(0xFF2196F3), // Blue
    Color(0xFF03A9F4), // Light Blue
    Color(0xFF00BCD4), // Cyan
    Color(0xFF009688), // Teal
    Color(0xFF4CAF50), // Green
    Color(0xFF8BC34A), // Light Green
    Color(0xFFCDDC39), // Lime
    Color(0xFFFFEB3B), // Yellow
    Color(0xFFFFC107), // Amber
    Color(0xFFFF9800), // Orange
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF795548), // Brown
    Color(0xFF9E9E9E), // Grey
    Color(0xFF607D8B), // Blue Grey
)
