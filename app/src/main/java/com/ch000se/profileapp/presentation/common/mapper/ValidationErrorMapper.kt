package com.ch000se.profileapp.presentation.common.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ch000se.profileapp.R
import com.ch000se.profileapp.domain.validation.ValidationError

@Composable
fun ValidationError.toMessage(): String = stringResource(
    when (this) {
        ValidationError.NameRequired -> R.string.error_name_required
        ValidationError.NameTooShort -> R.string.error_name_too_short
        ValidationError.SurnameRequired -> R.string.error_surname_required
        ValidationError.SurnameTooShort -> R.string.error_surname_too_short
        ValidationError.EmailRequired -> R.string.error_email_required
        ValidationError.EmailInvalid -> R.string.error_email_invalid
        ValidationError.PhoneRequired -> R.string.error_phone_required
        ValidationError.PhoneInvalid -> R.string.error_phone_invalid
        ValidationError.DateOfBirthdayRequired -> R.string.error_date_required
    }
)
