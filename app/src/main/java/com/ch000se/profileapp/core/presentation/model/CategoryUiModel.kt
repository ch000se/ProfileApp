package com.ch000se.profileapp.core.presentation.model

import com.ch000se.profileapp.domain.model.ContactCategory

sealed interface UiText {
    data class StringResource(val resId: Int) : UiText
    data class DynamicString(val value: String) : UiText
}

data class CategoryUiModel(
    val category: ContactCategory,
    val label: UiText,
    val isSelected: Boolean = false
)