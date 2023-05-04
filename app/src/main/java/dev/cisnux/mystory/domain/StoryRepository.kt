package dev.cisnux.mystory.domain

import android.net.Uri
import androidx.paging.PagingData
import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {
    fun getStories(): Flow<PagingData<Story>>
    suspend fun getStoriesForWidget(): List<Story>
    fun getDetailStory(id: String): Flow<ApplicationState<DetailStory>>
    suspend fun getPhotoFile(): File
    suspend fun convertUriToFile(uri: Uri): File
    fun postStory(file: File, description: String): Flow<ApplicationState<Nothing>>
    suspend fun rotatePhoto(file: File)
}