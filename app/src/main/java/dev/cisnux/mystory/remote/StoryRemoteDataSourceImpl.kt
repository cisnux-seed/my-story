package dev.cisnux.mystory.remote

import dev.cisnux.mystory.services.DetailStoryResponse
import dev.cisnux.mystory.services.StoryResponse
import dev.cisnux.mystory.services.StoryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRemoteDataSourceImpl @Inject constructor(private val service: StoryService) :
    StoryRemoteDataSource {
    override suspend fun getStories(token: String): StoryResponse = withContext(Dispatchers.IO) {
        service.getStories(token)
    }

    override suspend fun getDetailStory(token: String, id: String): DetailStoryResponse =
        withContext(Dispatchers.IO) {
            service.getDetailStory(
                token,
                id
            )
        }

    override suspend fun postStory(token: String, file: File, description: String) =
        withContext(Dispatchers.IO) {
            val requestDescription = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpg".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            service.postStory(token, imageMultipart, requestDescription)
        }
}