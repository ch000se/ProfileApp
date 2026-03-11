package com.ch000se.profileapp.presentation.edit.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ch000se.profileapp.R
import java.util.Calendar


private const val MIN_AGE = 4
private const val MAX_AGE = 120

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val maxYear = currentYear - MIN_AGE
    val minYear = currentYear - MAX_AGE

    val maxDateMillis = Calendar.getInstance().apply {
        set(maxYear, Calendar.DECEMBER, 31)
    }.timeInMillis

    val minDateMillis = Calendar.getInstance().apply {
        set(minYear, Calendar.JANUARY, 1)
    }.timeInMillis

    val initialDateMillis = Calendar.getInstance().apply {
        set(maxYear, Calendar.JANUARY, 1)
    }.timeInMillis

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        yearRange = minYear..maxYear,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in minDateMillis..maxDateMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year in minYear..maxYear
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onDateSelected(datePickerState.selectedDateMillis) }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}