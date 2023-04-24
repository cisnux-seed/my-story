package dev.cisnux.mystory.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dev.cisnux.mystory.ui.adapters.StoryAdapter

@Module
@InstallIn(ActivityComponent::class)
object StoryAdapterModule {

    @ActivityScoped
    @Provides
    fun provideStoryAdapter(): StoryAdapter = StoryAdapter()
}