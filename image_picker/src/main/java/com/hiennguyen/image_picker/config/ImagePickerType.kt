package com.hiennguyen.image_picker.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ImagePickerType : Parcelable {
    ONE_IMAGE, MULTIPLE_IMAGES
}