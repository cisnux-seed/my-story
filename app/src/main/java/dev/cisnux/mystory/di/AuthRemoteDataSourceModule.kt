package dev.cisnux.mystory.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.remote.AuthRemoteDataSource
import dev.cisnux.mystory.remote.AuthRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRemoteDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource
}