package com.ch000se.profileapp.core_ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.thenIf(
    condition: Boolean,
    modifier: @Composable Modifier.() -> Modifier
): Modifier = if (condition) then(modifier(Modifier)) else this
