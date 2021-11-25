package com.hiennguyen.image_picker.ui

import androidx.lifecycle.SavedStateHandle
import com.hiennguyen.image_picker.config.ImagePickerConfig
import com.hiennguyen.image_picker.config.ImagePickerType
import com.hiennguyen.image_picker.model.Image
import com.hiennguyen.image_picker.model.ImageFolder
import com.hiennguyen.image_picker.repository.ImageRepository
import com.hiennguyen.image_picker.repository.ResultWrapper
import com.hiennguyen.image_picker.ui.base.BaseViewModel
import com.hiennguyen.image_picker.ui.base.ViewIntent
import com.hiennguyen.image_picker.ui.base.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: ImageRepository
) : BaseViewModel<ImagePickerViewModel.ImagePickerIntent, ImagePickerViewModel.ImagePickerState>() {

    private var selectedImages = mutableListOf<Image>()

    //region Overrides
    override fun dispatchIntent(intent: ImagePickerIntent) {
        launchOnUi {
            when (intent) {
                is ImagePickerIntent.GetImageData -> {
                    repository.getImageData(intent.config).collect { result ->
                        _state.value = result.process(intent) { data ->
                            ImagePickerState.ResultImageData(data)
                        }
                    }
                }
            }
        }
    }

    override fun <T> ResultWrapper<T>.process(
        intent: ImagePickerIntent?,
        successFun: (T) -> ImagePickerState
    ): ImagePickerState {
        return when (this){
            ResultWrapper.Init -> ImagePickerState.Loading(intent = intent)
            is ResultWrapper.Success -> { successFun(this.data) }
            is ResultWrapper.Error -> ImagePickerState.Error(this.message, intent = intent)
            ResultWrapper.Done -> ImagePickerState.Done(intent = intent)
        }
    }
    //endregion

    //region Commons
    fun selectedImages(): List<Image> = selectedImages

    fun onSelectImage(image: Image, callback: (Image?, Image?) -> Unit) {
        val tempSelectedImages = selectedImages().toMutableList()

        when (pickerType()) {
            ImagePickerType.ONE_IMAGE -> {
                if (tempSelectedImages.contains(image)) {
                    tempSelectedImages.remove(image)
                    callback.invoke(image, null)
                } else {
                    val removedImage = tempSelectedImages.firstOrNull()
                    tempSelectedImages.removeFirstOrNull()
                    tempSelectedImages.add(image)
                    callback.invoke(removedImage, image)
                }
            }
            ImagePickerType.MULTIPLE_IMAGES -> {
                if (tempSelectedImages.contains(image)) {
                    tempSelectedImages.remove(image)
                    callback.invoke(image, null)
                } else {
                    if (tempSelectedImages.size >= config().maxImageNum) {
                        callback.invoke(null, null)
                        return
                    }
                    tempSelectedImages.add(image)
                    callback.invoke(null, image)
                }
            }
        }

        selectedImages = tempSelectedImages
    }

    fun savePickerType(type: ImagePickerType) {
        savedState.set("pickerType", type)
    }

    fun saveConfig(config: ImagePickerConfig) {
        savedState.set("config", config)
    }

    fun saveBucketId(bucketId: Int) {
        savedState.set("bucketId", bucketId)
    }

    fun pickerType() = savedState.get<ImagePickerType>("pickerType") ?: ImagePickerType.ONE_IMAGE
    fun config() = savedState.get<ImagePickerConfig>("config") ?: ImagePickerConfig()
    fun bucketId() = savedState.get<Int>("bucketId") ?: 0
    //endregion

    //region Intent, State
    sealed class ImagePickerState : ViewState {
        data class Loading(val intent: ImagePickerIntent? = null) : ImagePickerState()
        data class ResultImageData(val data: List<ImageFolder>) : ImagePickerState()
        data class Error(val message: String, val intent: ImagePickerIntent? = null) : ImagePickerState()
        data class Done(val intent: ImagePickerIntent? = null) : ImagePickerState()
    }

    sealed class ImagePickerIntent : ViewIntent {
        data class GetImageData(val config: ImagePickerConfig) : ImagePickerIntent()
    }
    //endregion
}