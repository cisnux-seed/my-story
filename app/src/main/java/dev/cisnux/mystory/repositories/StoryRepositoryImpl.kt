package dev.cisnux.mystory.repositories

import android.net.Uri
import dev.cisnux.mystory.domain.DetailStory
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.local.AuthLocalDataSource
import dev.cisnux.mystory.local.StoryLocalDataSource
import dev.cisnux.mystory.remote.StoryRemoteDataSource
import dev.cisnux.mystory.services.asDetailStory
import dev.cisnux.mystory.services.asStories
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.HTTP_FAILURES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    override fun getStories(): Flow<ApplicationState<List<Story>>> =
        flow {
            try {
                emit(ApplicationState.Loading())
                val token = authLocalDataSource.getToken()
                token?.let {
                    val response = remoteDataSource.getStories("Bearer $it")
                    emit(ApplicationState.Success(response.listStory.asStories()))
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

    override fun getDetailStory(id: String): Flow<ApplicationState<DetailStory>> =
        flow {
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

    override suspend fun getPhotoFile(): File =
        storyLocalDataSource.createStoryFile()

    override suspend fun convertUriToFile(uri: Uri): File = storyLocalDataSource.uriToFile(uri)

    override fun postStory(
        file: File,
        description: String
    ): Flow<ApplicationState<Nothing>> =
        flow {
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

    override suspend fun rotatePhoto(file: File) =
        storyLocalDataSource.rotateFile(file)
}