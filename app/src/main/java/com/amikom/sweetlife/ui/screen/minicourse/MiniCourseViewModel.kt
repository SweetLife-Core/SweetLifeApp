// package com.amikom.sweetlife.ui.screen.minicourse

package com.amikom.sweetlife.ui.screen.minicourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.remote.Result // Import Result
import com.amikom.sweetlife.data.remote.dto.minicourse.MiniCourseDto
import com.amikom.sweetlife.domain.repository.MiniCourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Ini representasi state UI untuk Composable
data class MiniCourseUiState(
    val isLoading: Boolean = false,
    val courses: List<MiniCourseDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MiniCourseViewModel @Inject constructor(
    private val repository: MiniCourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MiniCourseUiState())
    val uiState: StateFlow<MiniCourseUiState> = _uiState.asStateFlow()

    init {
        fetchCourses() // Langsung ambil data saat ViewModel dibuat
    }

    fun fetchCourses() {
        viewModelScope.launch {
            _uiState.value = MiniCourseUiState(isLoading = true) // Set state loading
            try {
                when (val result = repository.getMiniCourses()) {
                    is Result.Success -> {
                        _uiState.value = MiniCourseUiState(courses = result.data) // Update data sukses
                    }
                    is Result.Error -> {
                        _uiState.value = MiniCourseUiState(error = result.error) // Update error
                    }
                    else -> {
                        // Tidak perlu handle Result.Loading/Empty di sini karena kita sudah set di awal
                    }
                }
            } catch (e: Exception) {
                // Tangani exception yang mungkin tidak terbungkus Result dari repository
                _uiState.value = MiniCourseUiState(error = e.message ?: "Kesalahan tak terduga!")
            }
        }
    }
}