package com.hiennguyen.image_picker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hiennguyen.image_picker.R
import com.hiennguyen.image_picker.databinding.ItemImageBinding
import com.hiennguyen.image_picker.model.Image
import com.hiennguyen.image_picker.ui.base.BaseAdapter
import com.hiennguyen.image_picker.util.Utils.gone
import com.hiennguyen.image_picker.util.Utils.isGifFormat
import com.hiennguyen.image_picker.util.Utils.visible

class ImageAdapter(currentSelectedImages: List<Image>? = null) : BaseAdapter<Image, ImageAdapter.ImageViewHolder>() {

    private val selectedImages = mutableListOf<Image>()

    init {
        if (!currentSelectedImages.isNullOrEmpty()) selectedImages.addAll(currentSelectedImages)
    }

    //region Overrides
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(differ.currentList[position], isSelected(differ.currentList[position]))
    }
    //endregion

    //region Commons
    private fun isSelected(image: Image) = selectedImages.contains(image)

    fun updateSelectedImages(removedImage: Image?, addedImage: Image?) {
        when {
            removedImage != null && addedImage != null -> {
                selectedImages.remove(removedImage)
                selectedImages.add(addedImage)
                notifyItemChanged(data().indexOf(removedImage))
                notifyItemChanged(data().indexOf(addedImage))
            }
            removedImage != null -> {
                selectedImages.remove(removedImage)
                notifyItemChanged(data().indexOf(removedImage))
            }
            addedImage != null -> {
                selectedImages.add(addedImage)
                notifyItemChanged(data().indexOf(addedImage))
            }
        }
    }
    //endregion

    //region ViewHolder
    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Image, isSelected: Boolean) {
            binding.apply {
                ivThumbnail.load(data.contentUri) {
                    placeholder(R.drawable.image_placeholder)
                    error(R.drawable.image_placeholder)
                }

                viewAlpha.alpha = if (isSelected) 0.5f else 0f
                root.foreground = if (isSelected) ContextCompat.getDrawable(root.context, R.drawable.ic_action_done) else null

                if (isGifFormat(data.path)) tvGifType.visible() else tvGifType.gone()

                root.setOnClickListener {
                    if (absoluteAdapterPosition >= 0 && absoluteAdapterPosition < size()) {
                        onItemClickListener?.invoke(absoluteAdapterPosition, differ.currentList[absoluteAdapterPosition])
                    }
                }
            }
        }
    }
}