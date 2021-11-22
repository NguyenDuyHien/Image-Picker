package com.hiennguyen.image_picker.repository

sealed class ResultWrapper<out T> {
    object Init : ResultWrapper<Nothing>()
    object Done : ResultWrapper<Nothing>()
    data class Success<out T>(val data: T): ResultWrapper<T>()
    data class Error(val message: String, val error: Exception? = null): ResultWrapper<Nothing>()
}