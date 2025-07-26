package com.amikom.sweetlife.data.remote

sealed class Result<out T> {
    data object Empty: Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Result.Success) {
            action(data)
        }
        return this
    }

    fun <T> Result<T>.onFailure(action: (String) -> Unit): Result<T> {
        if (this is Result.Error) {
            action(error)
        }
        return this
    }

}