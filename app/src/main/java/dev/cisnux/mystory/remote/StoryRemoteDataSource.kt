package dev.cisnux.mystory.remote

import dev.cisnux.mystory.services.DetailStoryResponse
import dev.cisnux.mystory.services.StoryResponse
import java.io.File

interface StoryRemoteDataSource {
    suspend fun getStories(token: String): StoryResponse
    suspend fun getDetailStory(token: String, id: String): DetailStoryResponse
    suspend fun postStory(token: String, file: File, description: String)
}