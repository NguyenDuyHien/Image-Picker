package com.hiennguyen.image_picker.activity_result

import androidx.activity.result.ActivityResultLauncher
import com.hiennguyen.image_picker.config.ImagePickerConfig

object DefaultLauncher {

    fun ActivityResultLauncher<ImagePickerConfig>.launch(maxImageNum: Int = 1) {
        launch(ImagePickerConfig(maxImageNum), null)
    }
}