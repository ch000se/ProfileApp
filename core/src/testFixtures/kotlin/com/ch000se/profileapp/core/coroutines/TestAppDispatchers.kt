package com.ch000se.profileapp.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

class TestAppDispatchers(
    testDispatcher: TestDispatcher
) : AppDispatchers {
    override val io: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val mainImmediate: CoroutineDispatcher = testDispatcher
}