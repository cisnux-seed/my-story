package dev.cisnux.mystory.domain

import java.io.File

data class PostStory(
    val file: File,
    val description: String,
    val lat: Double?,
    val lon: Double?
)
