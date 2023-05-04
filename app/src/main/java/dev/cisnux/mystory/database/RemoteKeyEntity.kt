package dev.cisnux.mystory.database

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val TABLE_NAME = "remote_keys"

@Entity(tableName = TABLE_NAME)
data class RemoteKeyEntity(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)