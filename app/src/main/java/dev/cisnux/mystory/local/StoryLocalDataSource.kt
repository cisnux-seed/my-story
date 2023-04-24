package dev.cisnux.mystory.local

import android.net.Uri
import java.io.File

interface StoryLocalDataSource {
    suspend fun createStoryFile(): File
    suspend fun rotateFile(file: File)
    suspend fun reduceImage(file: File): File
    suspend fun uriToFile(uri: Uri): File
}