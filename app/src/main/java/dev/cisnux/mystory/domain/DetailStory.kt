package dev.cisnux.mystory.domain

data class DetailStory(
    val id: String,
    val photoUrl: String,
    val createdAt: String,
    val username: String,
    val description: String,
    val lat: Double?,
    val lon: Double?
)
