package dev.cisnux.mystory.remotes

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
    override suspend fun getStories(token: String, page: Int, size: Int): StoryResponse =
        withContext(Dispatchers.IO) {
            service.getStories(
                token = token,
                page = page,
                size = size
            )
        }

    override suspend fun getDetailStory(token: String, id: String): DetailStoryResponse =
        withContext(Dispatchers.IO) {
            service.getDetailStory(
                token,
                id
            )
        }

    override suspend fun postStory(
        token: String,
        file: File,
        description: String,
        lat: Double?,
        lon: Double?
    ) =
        withContext(Dispatchers.IO) {
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpg".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            service.postStory(
                token,
                imageMultipart,
                descriptionRequestBody,
                latRequestBody,
                lonRequestBody
            )
        }
}