package dev.cisnux.mystory.domain

import android.graphics.Bitmap

data class StoryMap(
    val id: String,
    val username: String,
    val photo: Bitmap,
    val lat: Double?,
    val lon: Double?
)
