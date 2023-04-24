package dev.cisnux.mystory.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.local.AuthLocalDataSource
import dev.cisnux.mystory.local.AuthLocalDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthLocalDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindTokenLocalDataSource(
        authLocalDataSourceImpl: AuthLocalDataSourceImpl
    ): AuthLocalDataSource
}