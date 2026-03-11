package com.ch000se.profileapp.di

import android.content.Context
import androidx.room.Room
import com.ch000se.profileapp.data.local.AppDatabase
import com.ch000se.profileapp.data.repository.UserRepositoryImpl
import com.ch000se.profileapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    @Singleton
    fun bindRepository(impl: UserRepositoryImpl): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "profile_database"
            ).build()
        }

        @Provides
        @Singleton
        fun provideUserDao(database: AppDatabase) = database.userDao()
    }

}