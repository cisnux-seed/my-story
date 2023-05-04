package dev.cisnux.mystory.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.remotes.StoryRemoteDataSource
import dev.cisnux.mystory.remotes.StoryRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoryRemoteDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindStoryRemoteDataSource(
        storyRemoteDataSourceImpl: StoryRemoteDataSourceImpl
    ): StoryRemoteDataSource
}