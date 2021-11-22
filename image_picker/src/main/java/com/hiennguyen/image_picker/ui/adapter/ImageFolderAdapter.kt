package com.hiennguyen.image_picker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hiennguyen.image_picker.R
import com.hiennguyen.image_picker.databinding.ItemImageFolderBinding
import com.hiennguyen.image_picker.model.ImageFolder
import com.hiennguyen.image_picker.ui.base.BaseAdapter

class ImageFolderAdapter : BaseAdapter<ImageFolder, ImageFolderAdapter.ImageFolderViewHolder>() {

    //region Overrides
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageFolderViewHolder {
        return ImageFolderViewHolder(ItemImageFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageFolderViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
    //endregion

    //region ViewHolder
    inner class ImageFolderViewHolder(private val binding: ItemImageFolderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ImageFolder) {
            binding.tvName.text = data.folderName
            binding.tvNumber.text = data.images.size.toString()

            data.images.firstOrNull()?.let {
                binding.ivThumbnail.load(it.contentUri) {
                    placeholder(R.drawable.image_placeholder)
                    error(R.drawable.image_placeholder)
                }
            }

            binding.root.setOnClickListener {
                if (absoluteAdapterPosition >= 0 && absoluteAdapterPosition < size()) {
                    onItemClickListener?.invoke(absoluteAdapterPosition, differ.currentList[absoluteAdapterPosition])
                }
            }
        }
    }
}