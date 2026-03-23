package com.ch000se.profileapp.data.local.internal

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

internal interface ImageFileManager {
    suspend fun copyImageToInternalStorage(url: String): String
    suspend fun deleteImage(url: String)
    fun isInternal(url: String): Boolean
}

internal class ImageFileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageFileManager {
    private val imagesDir: File = context.filesDir

    override suspend fun copyImageToInternalStorage(url: String): String {
        val imageName = "IMG_${UUID.randomUUID()}.jpg"
        val file = File(imagesDir, imageName)

        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(url.toUri())?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        return file.absolutePath
    }

    override suspend fun deleteImage(url: String) {
        withContext(Dispatchers.IO) {
            val file = File(url)
            if (file.exists() && isInternal(file.absolutePath)) {
                file.delete()
            }
        }
    }

    override fun isInternal(url: String): Boolean {
        return url.startsWith(imagesDir.absolutePath)
    }
}