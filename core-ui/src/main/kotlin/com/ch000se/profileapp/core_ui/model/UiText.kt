package com.ch000se.profileapp.core_ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class StringResource(@StringRes val resId: Int) : UiText
    data class DynamicString(val value: String) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.StringResource -> stringResource(resId)
    is UiText.DynamicString -> value
}
