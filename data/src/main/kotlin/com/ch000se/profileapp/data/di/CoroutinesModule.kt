package com.ch000se.profileapp.data.di

import com.ch000se.profileapp.core.coroutines.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @Provides
    @Singleton
    fun provideAppDispatchers(): AppDispatchers = object : AppDispatchers {
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val mainImmediate: CoroutineDispatcher = Dispatchers.Main.immediate
    }
}
