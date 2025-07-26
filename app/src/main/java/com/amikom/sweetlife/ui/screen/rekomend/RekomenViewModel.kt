
package com.amikom.sweetlife.ui.screen.rekomend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.remote.dto.rekomen.Data
import com.amikom.sweetlife.data.remote.dto.rekomen.ExerciseRecommendations
import com.amikom.sweetlife.data.remote.dto.rekomen.FoodRecommendation
import com.amikom.sweetlife.domain.repository.RekomenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.amikom.sweetlife.data.remote.Result as ApiResult


@HiltViewModel
class RekomenViewModel @Inject constructor(
    private val rekomendRepository: RekomenRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _foodRecommendations = MutableLiveData<List<FoodRecommendation>>()
    val foodRecommendations: LiveData<List<FoodRecommendation>> = _foodRecommendations

    private val _exerciseRecommendations = MutableLiveData<ExerciseRecommendations?>()
    val exerciseRecommendations: LiveData<ExerciseRecommendations?> = _exerciseRecommendations

    fun fetchRekomend() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _error.postValue(null)

            val result = rekomendRepository.fetchRekomend()

            when (result) {
                is ApiResult.Success -> {
                    Log.d("RekomenViewModel", "Success fetching recommendations ${result.data}")
                    val foods = result.data.data?.foodRecommendation
                    _foodRecommendations.postValue(foods ?: emptyList())


                    val exercises = result.data.data?.exerciseRecommendations
                    _exerciseRecommendations.postValue(exercises)
                    Log.d("RekomenViewModel", "Exercises: $exercises")
                }
                is ApiResult.Error -> {
                    // Hasil error, ambil pesan error dan post ke LiveData
                    _error.postValue(result.error)
                    Log.e("RekomenViewModel", "Error fetching recommendations: ${result.error}")
                }
                is ApiResult.Loading -> {
                    // Loading state, bisa ditangani jika diperlukan
                }
                else -> {
                    // Tidak ada data atau kasus lainnya
                    _error.postValue("Unknown error occurred")
                }
            }

            _isLoading.postValue(false)
        }
    }
}