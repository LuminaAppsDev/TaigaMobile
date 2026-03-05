package com.luminaapps.taigamobile.ui.components.pickers

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.ui.utils.clickableUnindicated
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Date picker with Compose M3 dialog. Null passed to onDatePicked() means selection was cleared.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    date: LocalDate?,
    onDatePicked: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes hintId: Int = R.string.date_hint,
    showClearButton: Boolean = true,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    onClose: () -> Unit = {},
    onOpen: () -> Unit = {}
) = Box {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = {
                showDialog = false
                onClose()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        datePickerState.selectedDateMillis?.let {
                            onDatePicked(
                                Instant.ofEpochMilli(it)
                                    .atOffset(ZoneOffset.UTC)
                                    .toLocalDate()
                            )
                        }
                        onClose()
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onClose()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(stringResource(R.string.select_date)) }
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {

        Text(
            text = date?.format(dateFormatter) ?: stringResource(hintId),
            style = style,
            modifier = Modifier.clickableUnindicated {
                onOpen()
                showDialog = true
            },
            color = date?.let { MaterialTheme.colorScheme.onSurface } ?: MaterialTheme.colorScheme.outline
        )

        if (showClearButton && date != null) {
            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = { onDatePicked(null) },
                modifier = Modifier.size(22.dp).clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
