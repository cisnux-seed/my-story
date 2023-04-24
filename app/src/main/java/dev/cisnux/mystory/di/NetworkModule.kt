package dev.cisnux.mystory.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(
    ): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor()
                .setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                )
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
}