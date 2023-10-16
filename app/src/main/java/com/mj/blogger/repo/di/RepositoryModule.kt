package com.mj.blogger.repo.di

import android.content.Context
import com.mj.blogger.repo.local.UserDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = UserDataStore(context)

    @Provides
    @Singleton
    fun provideRepository(
        dataStore: UserDataStore
    ): Repository = RepositoryImpl(dataStore)
}