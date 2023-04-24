package dev.cisnux.mystory.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.mystory.services.AuthService
import dev.cisnux.mystory.utils.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthServiceModule {

    @Singleton
    @Provides
    fun provideAuthService(
        client: OkHttpClient,
        moshiConverter: MoshiConverterFactory,
    ): AuthService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(moshiConverter)
            .client(client)
            .build()
            .create(AuthService::class.java)
    }
}