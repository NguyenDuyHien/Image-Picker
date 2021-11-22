package com.hiennguyen.image_picker.ui.base

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hiennguyen.image_picker.model.BaseFile

abstract class BaseAdapter<DataType: BaseFile, ViewHolderType: RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolderType>() {

    //region Variable
    open var onItemClickListener: ((Int, DataType) -> Unit)?= null

    protected open val differ: AsyncListDiffer<DataType> by lazy {
        AsyncListDiffer(this, object: DiffUtil.ItemCallback<DataType>() {
            override fun areItemsTheSame(oldItem: DataType, newItem: DataType): Boolean {
                return this@BaseAdapter.areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: DataType, newItem: DataType): Boolean {
                return this@BaseAdapter.areContentsTheSame(oldItem, newItem)
            }
        })
    }
    //endregion

    //region Override
    override fun getItemCount(): Int = differ.currentList.size
    //endregion

    //region Common
    protected open fun areItemsTheSame(oldItem: BaseFile, newItem: BaseFile) = oldItem.id == newItem.id
    protected open fun areContentsTheSame(oldItem: BaseFile, newItem: BaseFile) = oldItem == newItem

    fun bind(data: List<DataType>?, callback: Runnable ?= null) {
        differ.submitList(data, callback)
    }

    fun size() = differ.currentList.size
    fun data(): List<DataType> = differ.currentList
    fun item(pos: Int): DataType? = if (pos >= 0 && pos < size()) differ.currentList[pos] else null
    //endregion
}