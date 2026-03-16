package com.ch000se.profileapp.core.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ch000se.profileapp.core.presentation.model.UiText

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.StringResource -> stringResource(resId)
    is UiText.DynamicString -> value
}