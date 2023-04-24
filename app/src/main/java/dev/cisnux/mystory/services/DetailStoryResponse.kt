package dev.cisnux.mystory.services

import com.squareup.moshi.Json
import dev.cisnux.mystory.domain.DetailStory

data class DetailStoryResponse(
    @Json(name = "error")
    val error: Boolean,

    @Json(name = "message")
    val message: String,

    @Json(name = "story")
    val detailStoryItem: DetailStoryItem
)

data class DetailStoryItem(
    @Json(name = "photoUrl")
    val photoUrl: String,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "lon")
    val lon: Double?,

    @Json(name = "lat")
    val lat: Double?,

    @Json(name = "id")
    val id: String

)

fun DetailStoryItem.asDetailStory(): DetailStory = DetailStory(
    username = this.name,
    createdAt = this.createdAt,
    lat = this.lat,
    lon = this.lon,
    description = this.description,
    photoUrl = this.photoUrl,
    id = this.id
)
