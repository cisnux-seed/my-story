package dev.cisnux.mystory.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.cisnux.mystory.database.RemoteKeyEntity
import dev.cisnux.mystory.database.StoryEntity
import dev.cisnux.mystory.locals.AuthLocalDataSource
import dev.cisnux.mystory.locals.StoryLocalDataSource
import dev.cisnux.mystory.remotes.StoryRemoteDataSource
import dev.cisnux.mystory.services.asStoryEntities
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.HTTP_FAILURES
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator @Inject constructor(
    private val storyLocalDataSource: StoryLocalDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
    private val storyRemoteDataSource: StoryRemoteDataSource
) : RemoteMediator<Int, StoryEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        return try {
            val token = authLocalDataSource.getToken()
            token?.let {
                val page: Int = when (loadType) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
                    }

                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                        val prevKey = remoteKeys?.prevKey
                            ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                        prevKey
                    }

                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state)
                        val nextKey = remoteKeys?.nextKey
                            ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                        nextKey
                    }
                }

                val stories = storyRemoteDataSource.getStories(
                    token = "Bearer $token",
                    page = page,
                    size = state.config.pageSize
                ).listStory

                val endOfPaginationReached = stories.isEmpty()
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys =
                    stories.map { story -> RemoteKeyEntity(story.id, prevKey, nextKey) }

                storyLocalDataSource.onUpdateStories(
                    loadType == LoadType.REFRESH,
                    stories = stories.asStoryEntities(),
                    remoteKeys = remoteKeys
                )

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } ?: MediatorResult.Error(Exception())
        } catch (e: IOException) {
            MediatorResult.Error(Failure.ConnectionFailure())
        } catch (e: HttpException) {
            val statusCode = e.response()?.code()
            val failure = HTTP_FAILURES[statusCode]
            val errorBody = e.response()?.errorBody()?.string()
            errorBody?.let {
                failure?.message = JSONObject(it).getString("message")
            }
            MediatorResult.Error(failure ?: e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>) =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyLocalDataSource.getRemoteKeyById(data.id)
        }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyLocalDataSource.getRemoteKeyById((data.id))
        }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeyEntity? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyLocalDataSource.getRemoteKeyById(id)
            }
        }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}

