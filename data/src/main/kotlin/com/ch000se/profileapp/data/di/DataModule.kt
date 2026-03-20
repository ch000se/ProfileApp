package com.ch000se.profileapp.data.di

import com.ch000se.profileapp.data.local.internal.ImageFileManager
import com.ch000se.profileapp.data.local.internal.ImageFileManagerImpl
import com.ch000se.profileapp.data.repository.ContactRepositoryImpl
import com.ch000se.profileapp.data.repository.UserRepositoryImpl
import com.ch000se.profileapp.domain.repository.ContactRepository
import com.ch000se.profileapp.domain.repository.UserRepository
import com.ch000se.profileapp.domain.validation.UserValidator
import com.ch000se.profileapp.domain.validation.UserValidatorImpl
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

    @Binds
    @Singleton
    abstract fun bindImageFileManager(impl: ImageFileManagerImpl): ImageFileManager

    @Binds
    @Singleton
    abstract fun bindUserValidator(impl: UserValidatorImpl): UserValidator
}
