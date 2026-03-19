package com.ch000se.profileapp.data.di

import com.ch000se.profileapp.data.repository.ContactRepositoryImpl
import com.ch000se.profileapp.data.repository.UserRepositoryImpl
import com.ch000se.profileapp.domain.repository.ContactRepository
import com.ch000se.profileapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository
}
