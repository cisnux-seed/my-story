package dev.cisnux.mystory.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.local.StoryLocalDataSource
import dev.cisnux.mystory.local.StoryLocalDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoryLocalDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindStoryLocalDataSource(
        storyLocalDataSourceImpl: StoryLocalDataSourceImpl
    ): StoryLocalDataSource
}