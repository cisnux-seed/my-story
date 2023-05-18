package dev.cisnux.mystory

import dev.cisnux.mystory.domain.Story

object DataDummy {
    fun generateDummyStories(): List<Story> = List(100) { i ->
        Story(
            id = i.toString(),
            username = "john $i",
            description = "desc $i",
            photoUrl = "http://$i",
            createdAt = "2100-20-0$i",
            lat = i.toDouble(),
            lon = i.toDouble(),
        )
    }
}