package com.hiennguyen.image_picker.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagePickerConfig(
    val maxImageNum: Int = 1,
    val isEnableCamera: Boolean = true,
    val isIncludeAnimationImage: Boolean = false,
) : Parcelable
