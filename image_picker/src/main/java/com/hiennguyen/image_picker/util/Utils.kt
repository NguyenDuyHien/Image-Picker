package com.hiennguyen.image_picker.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.hiennguyen.image_picker.R
import com.hiennguyen.image_picker.model.Image
import java.io.File

object Utils {

    //region Common utils
    fun isReadExternalStoragePermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun Fragment.getAppSettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + requireContext().packageName)
        return intent
    }

    fun Context.checkCameraAvailability(): Boolean {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val isAvailable = intent.resolveActivity(packageManager) != null
        if (!isAvailable) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.msg_no_camera), Toast.LENGTH_SHORT).show()
        }
        return isAvailable
    }
    //endregion

    //region File utils
    fun checkFile(path: String?): Boolean {
        return if (path == null || path.isEmpty()) {
            false
        } else {
            try {
                val file = File(path)
                file.exists()
            } catch (ignored: Exception) {
                false
            }
        }
    }

    fun getFileName(path: String?): String? {
        path ?: return null
        return File(path).name
    }

    fun isGifFormat(path: String): Boolean {
        val extension = getExtension(path)
        return extension.equals("gif", ignoreCase = true)
    }

    private fun getExtension(path: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        if (extension.isNotBlank()) {
            return extension
        }
        return if (path.contains(".")) {
            path.substring(path.lastIndexOf(".") + 1, path.length)
        } else {
            ""
        }
    }

    fun Context.getCaptureImage(): Image {
        val fileName = "capture_image.jpg"
        val imageDir = File(applicationContext.filesDir.absolutePath + "/images")

        if (!imageDir.exists()) imageDir.mkdirs()

        val imageFile = File(imageDir, fileName)

        if (imageFile.exists()) imageFile.delete()
        imageFile.createNewFile()

        val contentUri = FileProvider.getUriForFile(this, applicationContext.packageName + ".image_picker.provider", imageFile)
        return Image(Constants.CAPTURE_IMAGE_ID, fileName, imageFile.absolutePath, contentUri.toString())
    }
    //endregion
}