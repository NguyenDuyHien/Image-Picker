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
import androidx.fragment.app.Fragment
import com.hiennguyen.image_picker.R

object Utils {

    fun isGifFormat(path: String): Boolean {
        val extension = getExtension(path)
        return extension.equals("gif", ignoreCase = true)
    }

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
}