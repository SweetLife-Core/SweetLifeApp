package com.amikom.sweetlife.ui.screen.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.minigrocery.GroceryItemDto
import com.amikom.sweetlife.domain.repository.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroceryUiState(
    val isLoading: Boolean = false,
    val grocery: List<GroceryItemDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(GroceryUiState())
    val uiState: StateFlow<GroceryUiState> = _uiState.asStateFlow()

    init {
        fetchGroceryItems() // Langsung ambil data saat ViewModel dibuat
    }

    fun fetchGroceryItems() {
        viewModelScope.launch {
            _uiState.value = GroceryUiState(isLoading = true) // Set state loading
            try {
                when (val result = repository.getMiniGrocery()) {
                    is com.amikom.sweetlife.data.remote.Result.Success -> {
                        _uiState.value = GroceryUiState(grocery = result.data) // Update data sukses
                    }
                    is com.amikom.sweetlife.data.remote.Result.Error -> {
                        _uiState.value = GroceryUiState(error = result.error) // <<< GANTI result.error JADI result.exception
                    }
                    else -> {
                        // Tidak perlu handle Result.Loading/Empty di sini karena kita sudah set di awal
                    }
                }
            } catch (e: Exception) {
                // Tangani exception yang mungkin tidak terbungkus Result dari repository
                _uiState.value = GroceryUiState(error = e.message ?: "Kesalahan tak terduga!")
            }
        }
    }
}