package com.ch000se.profileapp.presentation.common.model

import com.ch000se.profileapp.core_ui.model.UiText
import com.ch000se.profileapp.domain.model.ContactCategory

data class CategoryUiModel(
    val category: ContactCategory,
    val label: UiText,
    val isSelected: Boolean = false
)
