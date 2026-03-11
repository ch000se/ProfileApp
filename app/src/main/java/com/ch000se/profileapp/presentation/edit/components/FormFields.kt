package com.ch000se.profileapp.presentation.edit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core.presentation.mapper.toMessage
import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.presentation.edit.EditProfileUiAction
import com.ch000se.profileapp.presentation.edit.EditProfileUiState

@Composable
fun FormFields(
    uiState: EditProfileUiState,
    onAction: (EditProfileUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { onAction(EditProfileUiAction.UpdateName(it)) },
            label = { Text(stringResource(R.string.name_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isError = uiState.validationErrors.containsKey(UserField.NAME),
            supportingText = {
                uiState.validationErrors[UserField.NAME]?.let {
                    Text(it.toMessage(context))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.surname,
            onValueChange = { onAction(EditProfileUiAction.UpdateSurname(it)) },
            label = { Text(stringResource(R.string.surname_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isError = uiState.validationErrors.containsKey(UserField.SURNAME),
            supportingText = {
                uiState.validationErrors[UserField.SURNAME]?.let {
                    Text(it.toMessage(context))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onAction(EditProfileUiAction.UpdateEmail(it)) },
            label = { Text(stringResource(R.string.email_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            isError = uiState.validationErrors.containsKey(UserField.EMAIL),
            supportingText = {
                uiState.validationErrors[UserField.EMAIL]?.let {
                    Text(it.toMessage(context))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.phone,
            onValueChange = { onAction(EditProfileUiAction.UpdatePhone(it)) },
            label = { Text(stringResource(R.string.phone_label)) },
            placeholder = { Text(stringResource(R.string.phone_code)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null
                )
            },
            isError = uiState.validationErrors.containsKey(UserField.PHONE),
            supportingText = {
                uiState.validationErrors[UserField.PHONE]?.let {
                    Text(it.toMessage(context))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.dateOfBirthday,
            onValueChange = {},
            label = { Text(stringResource(R.string.date_of_birthday_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            },
            isError = uiState.validationErrors.containsKey(UserField.DATE_OF_BIRTHDAY),
            supportingText = {
                uiState.validationErrors[UserField.DATE_OF_BIRTHDAY]?.let {
                    Text(it.toMessage(context))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAction(EditProfileUiAction.ShowDatePicker) },
            readOnly = true,
            enabled = false,
            trailingIcon = {
                IconButton(onClick = { onAction(EditProfileUiAction.ShowDatePicker) }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            }
        )
    }
}
