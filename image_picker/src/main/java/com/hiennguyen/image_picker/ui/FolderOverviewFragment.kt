package com.hiennguyen.image_picker.ui

import android.Manifest
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hiennguyen.image_picker.R
import com.hiennguyen.image_picker.databinding.FragmentFolderOverviewBinding
import com.hiennguyen.image_picker.ui.ImagePickerViewModel.ImagePickerIntent.GetImageData
import com.hiennguyen.image_picker.ui.ImagePickerViewModel.ImagePickerState.*
import com.hiennguyen.image_picker.ui.adapter.ImageFolderAdapter
import com.hiennguyen.image_picker.ui.base.BaseFragment
import com.hiennguyen.image_picker.ui.decoration.GridSpacingItemDecoration
import com.hiennguyen.image_picker.util.Utils.getAppSettingsIntent
import com.hiennguyen.image_picker.util.Utils.gone
import com.hiennguyen.image_picker.util.Utils.isReadExternalStoragePermissionGranted
import com.hiennguyen.image_picker.util.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FolderOverviewFragment : BaseFragment<FragmentFolderOverviewBinding, ImagePickerViewModel.ImagePickerIntent, ImagePickerViewModel.ImagePickerState>() {

    //region Variables
    override val viewModel by activityViewModels<ImagePickerViewModel>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFolderOverviewBinding = FragmentFolderOverviewBinding::inflate
    private val imageFolderAdapter by lazy { ImageFolderAdapter() }

    private val activityForResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.dispatchIntent(GetImageData(viewModel.config()))
            }
        }
    //endregion

    //region Overrides
    override fun onResume() {
        super.onResume()
        loadImageData()
    }

    override fun bindComponent() {
        binding.rvImageFolder.apply {
            val spanCount = (layoutManager as? GridLayoutManager)?.spanCount ?: 1
            addItemDecoration(GridSpacingItemDecoration(spanCount, resources.getDimensionPixelSize(R.dimen.item_padding)))
            adapter = imageFolderAdapter
        }
    }

    override fun bindEvent() {
        imageFolderAdapter.onItemClickListener = { _, folder ->
            findNavController().navigate(FolderOverviewFragmentDirections.actionOverviewToDetail(folder.bucketId))
        }
    }

    override fun ImagePickerViewModel.ImagePickerState.toUI() {
        when (this) {
            is Loading -> showLoading(true)
            is ResultImageData -> {
                if (data.isEmpty()) {
                    showData(false)
                } else {
                    imageFolderAdapter.bind(data.sortedBy { it.name })
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
            isReadExternalStoragePermissionGranted(requireContext()) -> viewModel.dispatchIntent(GetImageData(viewModel.config()))
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> showNoPermissionNotify()
            else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun showData(isShow: Boolean, isError: Boolean = false) {
        if (isShow) {
            binding.tvInfo.gone()
            binding.rvImageFolder.visible()
        } else {
            binding.tvInfo.text = if (isError) getString(R.string.msg_load_image_error) else getString(R.string.msg_empty_image)
            binding.tvInfo.visible()
            binding.rvImageFolder.gone()
        }
    }

    private fun showNoPermissionNotify() {
        Snackbar
            .make(binding.root, R.string.msg_no_permission, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok) {
                activityForResultLauncher.launch(getAppSettingsIntent())
            }.show()
    }
    //endregion
}