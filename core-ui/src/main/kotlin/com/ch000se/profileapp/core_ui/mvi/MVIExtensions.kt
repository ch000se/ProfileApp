package com.ch000se.profileapp.core_ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.mvi.MVI

fun <UiState, UiAction, SideEffect, T> T.onStart(
    initialAction: () -> Unit = {}
) where T : ViewModel, T : MVI<UiState, UiAction, SideEffect> {
    setup(scope = viewModelScope, initialAction = initialAction)
}

fun <UiState, UiAction, SideEffect, T> T.emitSideEffect(
    effect: SideEffect
) where T : ViewModel, T : MVI<UiState, UiAction, SideEffect> {
    viewModelScope.emitSideEffect(effect)
}
