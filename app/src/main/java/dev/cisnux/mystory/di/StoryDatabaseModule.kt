package dev.cisnux.mystory.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.database.StoryDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoryDatabaseModule {
    private const val DATABASE_NAME = "story.db"

    @Singleton
    @Provides
    fun provideStoryDatabase(
        @ApplicationContext applicationContext: Context
    ): StoryDatabase {
        val storyDatabaseBuilder = Room.databaseBuilder(
            applicationContext,
            StoryDatabase::class.java,
            DATABASE_NAME
        )
        return storyDatabaseBuilder.build()
    }
}