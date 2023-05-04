package dev.cisnux.mystory.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.locals.StoryLocalDataSource
import dev.cisnux.mystory.locals.StoryLocalDataSourceImpl
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