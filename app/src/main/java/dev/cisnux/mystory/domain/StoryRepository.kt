package dev.cisnux.mystory.domain

import android.net.Uri
import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {
    fun getStories(): Flow<ApplicationState<List<Story>>>
    fun getDetailStory(id: String): Flow<ApplicationState<DetailStory>>
    suspend fun getPhotoFile(): File
    suspend fun convertUriToFile(uri: Uri): File
    fun postStory(file: File, description: String): Flow<ApplicationState<Nothing>>
    suspend fun rotatePhoto(file: File)
}