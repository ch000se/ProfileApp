package com.ch000se.profileapp.core.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface MVI<UiState, UiAction, SideEffect> {
    val uiState: StateFlow<UiState>
    val sideEffect: Flow<SideEffect>

    fun onAction(action: UiAction)

    fun MVI<UiState, UiAction, SideEffect>.updateUiState(reduce: UiState.() -> UiState)
    fun CoroutineScope.emitSideEffect(effect: SideEffect)

    fun setup(scope: CoroutineScope, initialAction: () -> Unit = {})
}

internal class MVIDelegate<UiState, UiAction, SideEffect>(
    initialState: UiState
) : MVI<UiState, UiAction, SideEffect> {

    private val _uiState = MutableStateFlow(initialState)
    private var scope: CoroutineScope? = null
    private var initialAction: (() -> Unit)? = null

    override val uiState: StateFlow<UiState> by lazy {
        scope?.let { coroutineScope ->
            initialAction?.let { action ->
                _uiState
                    .onStart { action() }
                    .stateIn(
                        coroutineScope,
                        SharingStarted.WhileSubscribed(5000),
                        _uiState.value
                    )
            }
        } ?: _uiState.asStateFlow()
    }

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    override val sideEffect: Flow<SideEffect> = _sideEffect.receiveAsFlow()

    override fun setup(scope: CoroutineScope, initialAction: () -> Unit) {
        this.scope = scope
        this.initialAction = initialAction
    }

    override fun onAction(action: UiAction) {}

    override fun MVI<UiState, UiAction, SideEffect>.updateUiState(reduce: UiState.() -> UiState) {
        _uiState.update { it.reduce() }
    }

    override fun CoroutineScope.emitSideEffect(effect: SideEffect) {
        launch { _sideEffect.send(effect) }
    }
}

fun <UiState, UiAction, SideEffect> mvi(
    initialState: UiState
): MVI<UiState, UiAction, SideEffect> = MVIDelegate(initialState)
