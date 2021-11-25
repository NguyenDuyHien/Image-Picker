package com.hiennguyen.image_picker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val fileId: Int,
    val fileName: String,
    val filePath: String,
    val contentUri: String
) : BaseFile(fileId, fileName, filePath), Parcelable