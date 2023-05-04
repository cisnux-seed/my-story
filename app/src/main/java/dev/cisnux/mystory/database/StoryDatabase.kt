package dev.cisnux.mystory.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StoryEntity::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = true
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}