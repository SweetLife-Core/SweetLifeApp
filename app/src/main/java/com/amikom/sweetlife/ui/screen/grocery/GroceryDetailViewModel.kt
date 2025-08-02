package com.amikom.sweetlife.ui.screen.grocery

import androidx.lifecycle.SavedStateHandle
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

@HiltViewModel
class GroceryDetailViewModel @Inject constructor(
    private val repository: GroceryRepository,
    savedStateHandle: SavedStateHandle // Dagger Hilt akan otomatis menyediakannya
) : ViewModel() {

    private val _uiState = MutableStateFlow<Result<GroceryItemDto>>(Result.Loading)
    val uiState: StateFlow<Result<GroceryItemDto>> = _uiState.asStateFlow()

    private val productId: Int = checkNotNull(savedStateHandle.get<Int>("productId"))

    init {
        fetchProductDetail(productId)
    }

    private fun fetchProductDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            _uiState.value = repository.getGroceryItemById(id)
        }
    }
}