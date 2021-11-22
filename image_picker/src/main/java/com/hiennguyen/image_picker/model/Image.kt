package com.hiennguyen.image_picker.model

import android.content.Context
import android.os.Parcelable
import androidx.core.content.FileProvider
import com.hiennguyen.image_picker.util.Constants
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class Image(
    val fileId: Int,
    val fileName: String,
    val filePath: String,
    val contentUri: String
) : BaseFile(fileId, fileName, filePath), Parcelable {

    companion object {

        fun Context.getCaptureImage(): Image {
            val fileName = "capture_image.jpg"
            val imageDir = File(applicationContext.filesDir.absolutePath + "/images")

            if (!imageDir.exists()) imageDir.mkdirs()

            val imageFile = File(imageDir, fileName)

            if (imageFile.exists()) imageFile.delete()
            imageFile.createNewFile()

            val contentUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".image_picker.provider", imageFile)
            return Image(Constants.CAPTURE_IMAGE_ID, fileName, imageFile.absolutePath, contentUri.toString())
        }
    }
}