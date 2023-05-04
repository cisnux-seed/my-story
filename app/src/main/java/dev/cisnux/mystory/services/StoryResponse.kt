package dev.cisnux.mystory.services

import com.squareup.moshi.Json
import dev.cisnux.mystory.database.StoryEntity

data class StoryResponse(

    @Json(name = "listStory")
    val listStory: List<ListStoryItem>,

    @Json(name = "error")
    val error: Boolean,

    @Json(name = "message")
    val message: String
)

data class ListStoryItem(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "photoUrl")
    val photoUrl: String,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "lat")
    val lat: Double,

    @Json(name = "lon")
    val lon: Double
)

fun List<ListStoryItem>.asStoryEntities(): List<StoryEntity> =
    map { (id, name, photoUrl, createdAt, lat, lon) ->
        StoryEntity(
            id = id,
            name = name,
            photoUrl = photoUrl,
            createdAt = createdAt,
            lat = lat,
            lon = lon
        )
    }