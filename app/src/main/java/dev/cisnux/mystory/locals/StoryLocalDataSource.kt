package dev.cisnux.mystory.locals

import android.net.Uri
import androidx.paging.PagingSource
import dev.cisnux.mystory.database.RemoteKeyEntity
import dev.cisnux.mystory.database.StoryEntity
import java.io.File

interface StoryLocalDataSource {
    suspend fun getRemoteKeyById(id: String): RemoteKeyEntity?
    suspend fun onUpdateStories(
        isRefresh: Boolean,
        stories: List<StoryEntity>,
        remoteKeys: List<RemoteKeyEntity>
    )
    fun getStoryEntities(): PagingSource<Int, StoryEntity>
    suspend fun getStoryForWidgets(): List<StoryEntity>
    suspend fun createStoryFile(): File
    suspend fun rotateFile(file: File)
    suspend fun reduceImage(file: File): File
    suspend fun uriToFile(uri: Uri): File
}