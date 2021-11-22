package com.hiennguyen.image_picker.model

import com.hiennguyen.image_picker.model.BaseFile
import com.hiennguyen.image_picker.model.Image

data class ImageFolder(
    val bucketId: Int,
    val folderName: String,
    val folderPath: String,
    val images: MutableList<Image> = mutableListOf()
) : BaseFile(bucketId, folderName, folderPath)