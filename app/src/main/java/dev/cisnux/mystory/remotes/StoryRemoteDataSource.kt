package dev.cisnux.mystory.remotes

import dev.cisnux.mystory.services.DetailStoryResponse
import dev.cisnux.mystory.services.StoryResponse
import java.io.File

interface StoryRemoteDataSource {
    suspend fun getStories(token: String, page: Int, size: Int): StoryResponse
    suspend fun getDetailStory(token: String, id: String): DetailStoryResponse
    suspend fun postStory(
        token: String,
        file: File,
        description: String,
        lat: Double?,
        lon: Double?
    )
}