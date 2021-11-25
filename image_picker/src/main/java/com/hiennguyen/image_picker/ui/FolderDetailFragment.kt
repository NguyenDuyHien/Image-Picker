package com.hiennguyen.image_picker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.hiennguyen.image_picker.R
import com.hiennguyen.image_picker.databinding.FragmentFolderDetailBinding
import com.hiennguyen.image_picker.ui.ImagePickerViewModel.ImagePickerIntent.GetImageData
import com.hiennguyen.image_picker.ui.ImagePickerViewModel.ImagePickerState.*
import com.hiennguyen.image_picker.ui.adapter.ImageAdapter
import com.hiennguyen.image_picker.ui.base.BaseFragment
import com.hiennguyen.image_picker.ui.decoration.GridSpacingItemDecoration
import com.hiennguyen.image_picker.util.Utils.gone
import com.hiennguyen.image_picker.util.Utils.isReadExternalStoragePermissionGranted
import com.hiennguyen.image_picker.util.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FolderDetailFragment : BaseFragment<FragmentFolderDetailBinding, ImagePickerViewModel.ImagePickerIntent, ImagePickerViewModel.ImagePickerState>() {

    //region Variables
    override val viewModel by activityViewModels<ImagePickerViewModel>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFolderDetailBinding = FragmentFolderDetailBinding::inflate
    private val safeArgs by navArgs<FolderDetailFragmentArgs>()
    private val imageAdapter by lazy { ImageAdapter(viewModel.selectedImages()) }
    //endregion

    //region Overrides
    override fun onResume() {
        super.onResume()
        loadImageData()
    }

    override fun initData() {
        viewModel.saveBucketId(safeArgs.bucketId)
    }

    override fun bindComponent() {
        binding.rvImage.apply {
            val spanCount = (layoutManager as? GridLayoutManager)?.spanCount ?: 1
            addItemDecoration(GridSpacingItemDecoration(spanCount, resources.getDimensionPixelSize(R.dimen.item_padding)))
            itemAnimator = null
            adapter = imageAdapter
        }
    }

    override fun bindEvent() {
        imageAdapter.onItemClickListener = { _, image ->
            viewModel.onSelectImage(image) { removedImage, addedImage ->
                if (removedImage != null || addedImage != null) {
                    imageAdapter.updateSelectedImages(removedImage, addedImage)
                    activity?.invalidateOptionsMenu()
                } else {
                    Toast.makeText(context, getString(R.string.msg_maximum_image_reach), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun ImagePickerViewModel.ImagePickerState.toUI() {
        when (this) {
            is Loading -> showLoading(true)
            is ResultImageData -> {
                val selectedFolder = data.firstOrNull { it.bucketId == viewModel.bucketId() }
                if (selectedFolder == null || selectedFolder.images.isEmpty()) {
                    showData(false)
                } else {
                    imageAdapter.bind(selectedFolder.images)
                    showData(true)
                }
            }
            is Error -> {
                showData(isShow = false, isError = true)
                Timber.d("Image error $message")
            }
            is Done -> showLoading(false)
        }
    }
    //endregion

    //region Commons
    private fun loadImageData() {
        when {
            isReadExternalStoragePermissionGranted(requireContext()) -> {
                viewModel.dispatchIntent(GetImageData(viewModel.config()))
            }
            else -> findNavController().popBackStack()
        }
    }

    private fun showData(isShow: Boolean, isError: Boolean = false) {
        if (isShow) {
            binding.tvInfo.gone()
            binding.rvImage.visible()
        } else {
            binding.tvInfo.text = if (isError) getString(R.string.msg_load_image_error) else getString(R.string.msg_empty_image)
            binding.tvInfo.visible()
            binding.rvImage.gone()
        }
    }
    //endregion
}