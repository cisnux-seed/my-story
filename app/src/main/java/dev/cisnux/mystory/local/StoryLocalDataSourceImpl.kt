package dev.cisnux.mystory.local

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class StoryLocalDataSourceImpl @Inject constructor(
    private val application: Application,
) :
    StoryLocalDataSource {
    override suspend fun createStoryFile(): File = withContext(Dispatchers.IO) {
        val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    override suspend fun rotateFile(file: File): Unit =
        withContext(Dispatchers.IO) {
            val matrix = Matrix()
            val bitmap = BitmapFactory.decodeFile(file.path)
            matrix.postRotate(90F)
            val result = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
            result.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                FileOutputStream(file)
            )
        }

    override suspend fun reduceImage(file: File): File = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bitmapStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bitmapStream)
            val bitmapPictureByteArray = bitmapStream.toByteArray()
            streamLength = bitmapPictureByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        file
    }

    override suspend fun uriToFile(uri: Uri): File = withContext(Dispatchers.IO) {
        val contentResolver = application.contentResolver
        val myFile = createStoryFile()

        val inputStream = contentResolver.openInputStream(uri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also {
                len = it
            } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        myFile
    }

    companion object {
        private const val FILENAME_FORMAT = "dd-MMM-yyyy"
    }
}