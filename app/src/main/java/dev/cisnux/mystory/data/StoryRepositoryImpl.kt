package dev.cisnux.mystory.data

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.cisnux.mystory.database.StoryEntity
import dev.cisnux.mystory.database.asStories
import dev.cisnux.mystory.database.asStory
import dev.cisnux.mystory.domain.DetailStory
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.locals.AuthLocalDataSource
import dev.cisnux.mystory.locals.StoryLocalDataSource
import dev.cisnux.mystory.remotes.StoryRemoteDataSource
import dev.cisnux.mystory.services.asDetailStory
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.HTTP_FAILURES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource,
    private val storyLocalDataSource: StoryLocalDataSource,
    private val remoteDataSource: StoryRemoteDataSource
) : StoryRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(): Flow<PagingData<Story>> =
        Pager(config = PagingConfig(pageSize = 10), remoteMediator = StoryRemoteMediator(
            storyLocalDataSource, authLocalDataSource, remoteDataSource
        ), pagingSourceFactory = {
            storyLocalDataSource.getStoryEntities()
        }).flow.map {
            it.map(StoryEntity::asStory)
        }

    override suspend fun getStoriesForWidget(): List<Story> =
        storyLocalDataSource.getStoryForWidgets().asStories()

    override fun getDetailStory(id: String): Flow<ApplicationState<DetailStory>> = flow {
        try {
            emit(ApplicationState.Loading())
            val token = authLocalDataSource.getToken()
            token?.let {
                val response = remoteDataSource.getDetailStory("Bearer $it", id)
                emit(ApplicationState.Success(response.detailStoryItem.asDetailStory()))
            }
        } catch (e: IOException) {
            emit(ApplicationState.Failed(Failure.ConnectionFailure()))
        } catch (e: HttpException) {
            val statusCode = e.response()?.code()
            val failure = HTTP_FAILURES[statusCode]
            val errorBody = e.response()?.errorBody()?.string()
            errorBody?.let {
                failure?.message = JSONObject(it).getString("message")
            }
            failure?.let {
                emit(ApplicationState.Failed(it))
            }
        }
    }

    override suspend fun getPhotoFile(): File = storyLocalDataSource.createStoryFile()

    override suspend fun convertUriToFile(uri: Uri): File = storyLocalDataSource.uriToFile(uri)

    override fun postStory(
        file: File, description: String
    ): Flow<ApplicationState<Nothing>> = flow {
        try {
            val photoCompress = storyLocalDataSource.reduceImage(file)
            emit(ApplicationState.Loading())
            val token = authLocalDataSource.getToken()
            token?.let {
                remoteDataSource.postStory("Bearer $it", photoCompress, description)
                emit(ApplicationState.Success(null))
            }
        } catch (e: IOException) {
            emit(ApplicationState.Failed(Failure.ConnectionFailure()))
        } catch (e: HttpException) {
            val statusCode = e.response()?.code()
            val failure = HTTP_FAILURES[statusCode]
            val errorBody = e.response()?.errorBody()?.string()
            errorBody?.let {
                failure?.message = JSONObject(it).getString("message")
            }
            failure?.let {
                emit(ApplicationState.Failed(it))
            }
        }
    }

    override suspend fun rotatePhoto(file: File) = storyLocalDataSource.rotateFile(file)
}