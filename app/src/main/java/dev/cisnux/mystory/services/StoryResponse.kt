package dev.cisnux.mystory.services

import com.squareup.moshi.Json
import dev.cisnux.mystory.domain.Story

data class StoryResponse(

    @Json(name = "listStory")
    val listStory: List<ListStoryItem>,

    @Json(name = "error")
    val error: Boolean,

    @Json(name = "message")
    val message: String
)

data class ListStoryItem(

    @Json(name = "photoUrl")
    val photoUrl: String,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "id")
    val id: String,
)


fun List<ListStoryItem>.asStories(): List<Story> = map {
    Story(
        id = it.id,
        username = it.name,
        photoUrl = it.photoUrl,
        createdAt = it.createdAt
    )
}