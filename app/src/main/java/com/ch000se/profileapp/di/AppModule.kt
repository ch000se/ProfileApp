package com.ch000se.profileapp.di

import android.content.Context
import androidx.room.Room
import com.ch000se.ninjauser.data.remote.RandomUserApi
import com.ch000se.profileapp.data.local.AppDatabase
import com.ch000se.profileapp.data.remote.interceptor.AuthInterceptor
import com.ch000se.profileapp.data.repository.ContactRepositoryImpl
import com.ch000se.profileapp.data.repository.UserRepositoryImpl
import com.ch000se.profileapp.domain.repository.ContactRepository
import com.ch000se.profileapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository

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
            ).fallbackToDestructiveMigration(true).build()
        }

        @Provides
        @Singleton
        fun provideUserDao(database: AppDatabase) = database.userDao()

        @Provides
        @Singleton
        fun provideContactDao(database: AppDatabase) = database.contactDao()

        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        @Provides
        @Singleton
        fun provideConverterFactory(json: Json): Converter.Factory =
            json.asConverterFactory("application/json".toMediaType())

        @Provides
        @Singleton
        fun provideOkHttpClient(
            authInterceptor: AuthInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
        }

        @Provides
        @Singleton
        fun providesRetrofit(
            okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.api-ninjas.com/")
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(
            retrofit: Retrofit
        ): RandomUserApi {
            return retrofit.create()
        }
    }
}