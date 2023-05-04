package dev.cisnux.mystory.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.cisnux.mystory.domain.Story

private const val TABLE_NAME = "story"

@Entity(tableName = TABLE_NAME)
data class StoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    val lat: Double,
    val lon: Double
)

fun StoryEntity.asStory(): Story = Story(
    id = id,
    username = name,
    photoUrl = photoUrl,
    createdAt = createdAt,
    lat = lat,
    lon = lon
)

fun List<StoryEntity>.asStories(): List<Story> = map { (id, name, photoUrl, createdAt, lat, lon) ->
    Story(
        id = id,
        username = name,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon
    )
}
