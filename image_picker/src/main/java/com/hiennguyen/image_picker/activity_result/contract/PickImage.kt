package com.hiennguyen.image_picker.activity_result.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import com.hiennguyen.image_picker.config.ImagePickerConfig
import com.hiennguyen.image_picker.config.ImagePickerType
import com.hiennguyen.image_picker.model.Image
import com.hiennguyen.image_picker.ui.ImagePickerActivity
import com.hiennguyen.image_picker.util.Constants

class PickImage : ActivityResultContract<ImagePickerConfig, Image?>() {

    override fun createIntent(context: Context, input: ImagePickerConfig): Intent {
        return Intent(context, ImagePickerActivity::class.java)
            .putExtra(Constants.IMAGE_PICKER_TYPE, ImagePickerType.ONE_IMAGE as Parcelable)
            .putExtra(Constants.IMAGE_PICKER_CONFIG, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Image? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.getParcelableExtra(
            Constants.IMAGE_RESULT)
    }
}