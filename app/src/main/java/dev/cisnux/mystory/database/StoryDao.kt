package dev.cisnux.mystory.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(users: List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getStoryEntities(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteStories()

    @Query("SELECT * FROM story")
    suspend fun getStoryEntitiesForWidget(): List<StoryEntity>
}