package com.hiennguyen.image_picker.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.hiennguyen.image_picker.config.ImagePickerConfig
import com.hiennguyen.image_picker.config.ImagePickerType
import com.hiennguyen.image_picker.R
import com.hiennguyen.image_picker.databinding.ActivityImagePickerBinding
import com.hiennguyen.image_picker.model.Image
import com.hiennguyen.image_picker.model.Image.Companion.getCaptureImage
import com.hiennguyen.image_picker.util.Constants
import com.hiennguyen.image_picker.util.Utils.checkCameraAvailability
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImagePickerActivity : AppCompatActivity() {

    //region Variables
    private val viewModel by viewModels<ImagePickerViewModel>()
    private lateinit var binding: ActivityImagePickerBinding
    private val captureImage by lazy { getCaptureImage() }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved) {
            finishWithResult(listOf(captureImage))
        }
    }
    //endregion

    //region Overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindIntent()
        bindComponent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.image_picker_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_camera)?.isVisible = viewModel.config().isEnableCamera
        menu?.findItem(R.id.menu_done)?.isVisible = viewModel.selectedImages().isNotEmpty()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_camera -> {
                if (checkCameraAvailability()) {
                    takePhotoLauncher.launch(captureImage.contentUri.toUri())
                }
                true
            }
            R.id.menu_done -> {
                finishWithResult(viewModel.selectedImages())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //endregion

    //region Commons
    fun showLoading(isShow: Boolean) {
        binding.pbLoading.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun bindIntent() {
        val pickerType = intent.getParcelableExtra<ImagePickerType>(Constants.IMAGE_PICKER_TYPE)
        val config = intent.getParcelableExtra<ImagePickerConfig>(Constants.IMAGE_PICKER_CONFIG)

        viewModel.savePickerType(requireNotNull(pickerType))
        viewModel.saveConfig(requireNotNull(config))
    }

    private fun bindComponent() {
        setSupportActionBar(binding.toolbar)

        findChildNavController(R.id.nav_host_fragment)?.let { navController ->
            val appBarConfiguration = AppBarConfiguration(
                topLevelDestinationIds = setOf(),
                fallbackOnNavigateUpListener = {
                    finishWithoutResult()
                    true
                }
            )
            binding.toolbar.setupWithNavController(navController, appBarConfiguration)

            navController.addOnDestinationChangedListener { _, _, _ ->
                binding.toolbar.setNavigationIcon(R.drawable.ic_action_back)
            }
        }
    }

    private fun finishWithoutResult() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun finishWithResult(selectedImages: List<Image>) {
        val data = Intent()
        when (viewModel.pickerType()) {
            ImagePickerType.ONE_IMAGE -> data.putExtra(Constants.IMAGE_RESULT, selectedImages.firstOrNull())
            ImagePickerType.MULTIPLE_IMAGES -> data.putParcelableArrayListExtra(Constants.MULTIPLE_IMAGES_RESULT, ArrayList(selectedImages))
        }
        setResult(RESULT_OK, data)
        finish()
    }

    private fun AppCompatActivity.findChildNavController(@IdRes navHostId: Int): NavController? {
        return (supportFragmentManager.findFragmentById(navHostId) as? NavHostFragment)?.findNavController()
    }
    //endregion
}