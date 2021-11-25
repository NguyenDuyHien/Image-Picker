package com.hiennguyen.image_picker.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.hiennguyen.image_picker.config.ImagePickerConfig
import com.hiennguyen.image_picker.model.Image
import com.hiennguyen.image_picker.model.ImageFolder
import com.hiennguyen.image_picker.util.Utils.checkFile
import com.hiennguyen.image_picker.util.Utils.getFileName
import com.hiennguyen.image_picker.util.Utils.isGifFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(@ApplicationContext private val appContext: Context) {
    private val externalContentUri by lazy { MediaStore.Images.Media.EXTERNAL_CONTENT_URI }

    private val projections by lazy {
        arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
        )
    }

    suspend fun getImageData(config: ImagePickerConfig) = flow<ResultWrapper<List<ImageFolder>>> {
        emit(ResultWrapper.Init)

        val imageFolders = mutableListOf<ImageFolder>()
        val imagePaths = mutableListOf<Int>()

        val cursor = appContext.contentResolver.query(
            externalContentUri, projections, null, null,
            "LOWER (" + MediaStore.Images.Media.DATE_TAKEN + ") DESC"
        )

        if (cursor != null && cursor.moveToFirst()) {
            try {
                do {
                    // Get image data
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                    // Check path and file extension
                    if (!checkFile(path) || (isGifFormat(path) && !config.isIncludeAnimationImage)) continue

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                    val contentUri = Uri.withAppendedPath(externalContentUri, id.toString()).toString()

                    // Check file name
                    val fileName = when {
                        name != null -> name
                        getFileName(path) != null -> getFileName(path)
                        else -> null
                    }
                    if (fileName.isNullOrBlank()) continue

                    val image = Image(id, fileName, path, contentUri)

                    // Get folder data
                    var bucket = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val bucketId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))

                    // Check folder name
                    if (bucket == null) {
                        val parent = File(path).parentFile
                        bucket = if (parent != null) {
                            parent.name
                        } else {
                            DEFAULT_NAME
                        }
                    }

                    // Check folder path
                    val folderPath = if (bucket == DEFAULT_NAME) {
                        ""
                    } else {
                        val parentPath = path.substring(0, path.lastIndexOf(bucket + File.separator))
                        parentPath + bucket + File.separator
                    }

                    // Add folder to list
                    if (!imagePaths.contains(bucketId)) {
                        imagePaths.add(bucketId)
                        val imageFolder = ImageFolder(bucketId, bucket, folderPath)
                        imageFolder.images.add(image)
                        imageFolders.add(imageFolder)
                    } else {
                        for (folder in imageFolders) {
                            if (folder.bucketId == bucketId) {
                                folder.images.add(image)
                            }
                        }
                    }
                } while (cursor.moveToNext())

                cursor.close()
                emit(ResultWrapper.Success(imageFolders))
            } catch (e: Exception) {
                Timber.e("Load image failed: ${e.stackTraceToString()}")
                emit(ResultWrapper.Error(e.stackTraceToString()))
            }
        } else {
            emit(ResultWrapper.Success(imageFolders))
        }

        emit(ResultWrapper.Done)
    }.flowOn(Dispatchers.IO)

    companion object {
        private const val DEFAULT_NAME = "Unknown"
    }
}