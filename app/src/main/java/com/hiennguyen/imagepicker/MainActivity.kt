package com.hiennguyen.imagepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import coil.load
import com.hiennguyen.image_picker.activity_result.DefaultLauncher.launch
import com.hiennguyen.image_picker.activity_result.contract.PickImage
import com.hiennguyen.imagepicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val pickImageLauncher = registerForActivityResult(PickImage()) { pickedImage ->
        pickedImage?.let {
            binding.ivPreview.load(pickedImage.contentUri.toUri())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch()
        }
    }
}