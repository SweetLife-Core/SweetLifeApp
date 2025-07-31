package com.amikom.sweetlife.ui.screen.scan

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.scan.ScanResponse
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.domain.usecases.dashboard.DashboardUseCases
import com.amikom.sweetlife.util.bitmapToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class CameraScanViewModel @Inject constructor(
    private val dashboardUseCases: DashboardUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _scanState = MutableLiveData<Result<ScanResponse>>(Result.Empty)
    val scanData: LiveData<Result<ScanResponse>> = _scanState

    init {
        viewModelScope.launch {
            authUseCases.checkIsUserLogin().collect { isLoggedIn ->
                _isUserLoggedIn.value = isLoggedIn
            }
        }
    }

    fun resetResult() {
        _scanState.value = Result.Empty
    }

    // Ini yang paling parah, "uploadScan"
    fun uploadScan(context: Context, bitmap: Bitmap) { // Hapus onResult, biar ViewModel yang urus
        _scanState.value = Result.Loading // Tambahin ini biar tahu lagi loading
        viewModelScope.launch {
            try {
                val file = bitmapToFile(context, bitmap, "scan_result.jpg")
                Log.d("UPLOAD_SCAN", "Uploading file: ${file.name}, Loc: ${file.path}, Size: ${file.length()/1000} KB")

                val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, reqFile)
                _scanState.value = dashboardUseCases.scanFood(multipartBody)
            } catch (e: Exception) {
                Log.e("UPLOAD_SCAN", "Exception occurred: ${e.message}", e)
                _scanState.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }
}