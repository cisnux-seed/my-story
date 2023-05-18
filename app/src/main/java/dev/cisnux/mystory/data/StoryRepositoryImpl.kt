package dev.cisnux.mystory.data

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.cisnux.mystory.database.StoryEntity
import dev.cisnux.mystory.database.asStories
import dev.cisnux.mystory.database.asStory
import dev.cisnux.mystory.domain.DetailStory
import dev.cisnux.mystory.domain.PostStory
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.domain.StoryMap
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.locals.AuthLocalDataSource
import dev.cisnux.mystory.locals.StoryLocalDataSource
import dev.cisnux.mystory.remotes.StoryRemoteDataSource
import dev.cisnux.mystory.services.asDetailStory
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.HTTP_FAILURES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authLocalDataSource: AuthLocalDataSource,
    private val storyLocalDataSource: StoryLocalDataSource,
    private val remoteDataSource: StoryRemoteDataSource
) : StoryRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(): Flow<PagingData<Story>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(
                storyLocalDataSource, authLocalDataSource, remoteDataSource
            ), pagingSourceFactory = {
                storyLocalDataSource.getStoryEntities()
            }).flow.map {
            it.map(StoryEntity::asStory)
        }

    override suspend fun getStoriesForWidget(): List<Story> =
        storyLocalDataSource.getStoryListForWidget().asStories()

    override fun getStoriesForMap(): Flow<List<StoryMap>> =
        storyLocalDataSource.getStoryListForMap().map { storyEntities ->
            storyEntities.filter {
                it.lat != null && it.lon != null
            }.map { (id, name, _, photoUrl, _, lat, lon) ->
                val bitmapPhoto = (context.imageLoader.execute(
                    ImageRequest.Builder(context).data(photoUrl)
                        .transformations(CircleCropTransformation())
                        .size(68, 68)
                        .allowConversionToBitmap(true)
                        .dispatcher(Dispatchers.IO)
                        .build()
                ).drawable as BitmapDrawable).bitmap
                StoryMap(
                    id,
                    name,
                    bitmapPhoto,
                    lat,
                    lon
                )
            }
        }

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
        postStory: PostStory
    ): Flow<ApplicationState<Nothing>> = flow {
        try {
            emit(ApplicationState.Loading())
            val token = authLocalDataSource.getToken()
            token?.let {
                val photoCompress = storyLocalDataSource.reduceImage(postStory.file)
                remoteDataSource.postStory(
                    "Bearer $it",
                    photoCompress,
                    postStory.description,
                    postStory.lat,
                    postStory.lon
                )
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