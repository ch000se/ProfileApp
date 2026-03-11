package com.ch000se.profileapp.core.presentation.mapper

import android.content.Context
import com.ch000se.profileapp.R
import com.ch000se.profileapp.domain.validation.ValidationError

fun ValidationError.toMessage(context: Context): String = when (this) {
    ValidationError.NameRequired -> context.getString(R.string.error_name_required)
    ValidationError.NameTooShort -> context.getString(R.string.error_name_too_short)
    ValidationError.SurnameRequired -> context.getString(R.string.error_surname_required)
    ValidationError.SurnameTooShort -> context.getString(R.string.error_surname_too_short)
    ValidationError.EmailRequired -> context.getString(R.string.error_email_required)
    ValidationError.EmailInvalid -> context.getString(R.string.error_email_invalid)
    ValidationError.PhoneRequired -> context.getString(R.string.error_phone_required)
    ValidationError.PhoneInvalid -> context.getString(R.string.error_phone_invalid)
    ValidationError.DateOfBirthdayRequired -> context.getString(R.string.error_date_required)
}