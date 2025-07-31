package com.amikom.sweetlife.ui.screen.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.model.FoodRequest
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.scan.FindFoodResponse
import com.amikom.sweetlife.data.remote.dto.scan.SaveFoodResponse
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.domain.usecases.dashboard.DashboardUseCases
import com.amikom.sweetlife.domain.usecases.dashboard.FindFood
import com.amikom.sweetlife.domain.usecases.dashboard.SaveFood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultScanViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val dashboardUseCases: DashboardUseCases,
    private val findFoodUseCase: FindFood,
    private val saveFoodUseCase: SaveFood,
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _findFoodState = MutableLiveData<Result<FindFoodResponse>>(Result.Empty)
    val findFoodData: LiveData<Result<FindFoodResponse>> = _findFoodState

    private val _navigateToDashboard = Channel<Unit>(Channel.BUFFERED)
    val navigateToDashboardEvent = _navigateToDashboard.receiveAsFlow()

    private val _saveFoodState = MutableLiveData<Result<SaveFoodResponse>>(Result.Empty)
    val saveFoodData: LiveData<Result<SaveFoodResponse>> = _saveFoodState

    init {
        viewModelScope.launch {
            authUseCases.checkIsUserLogin().collect { isLoggedIn ->
                _isUserLoggedIn.value = isLoggedIn
            }
        }
    }

    fun findFood(name: String, weight: Int) {
        if (_findFoodState.value !is Result.Loading) {
            viewModelScope.launch {
                _findFoodState.postValue(Result.Loading)
                try {
                    val result = findFoodUseCase(name, weight)
                    if (result is Result.Success && result.data.data != null) {
                        val foodData = result.data.data
                        // Cek apakah semua nilai nutrisi NOL
                        if (foodData.calories == 0.0 && foodData.fat == 0.0 &&
                            foodData.carbohydrates == 0.0 && foodData.sugar == 0.0 &&
                            foodData.protein == 0.0) {
                            // Jika nol semua, anggap ini sebagai NOT FOUND atau ERROR
                            _findFoodState.postValue(Result.Error("Food '$name' not found or has no nutritional data."))
                        } else {
                            _findFoodState.postValue(result)
                        }
                    } else if (result is Result.Success && result.data.data == null) {
                        _findFoodState.postValue(Result.Error("Food '$name' not found or server returned empty data."))
                    }
                    else {
                        _findFoodState.postValue(result)
                    }

                } catch (e: Exception) {
                    _findFoodState.postValue(Result.Error(e.message ?: "Unexpected Error"))
                }
            }
        }
    }

    fun resetFindState() {
        _findFoodState.postValue(Result.Empty)
    }

    fun resetSaveState() {
        _saveFoodState.postValue(Result.Empty)
    }

    fun saveFood(listFood: FoodRequest) {
        if (_saveFoodState.value !is Result.Loading) {
            viewModelScope.launch {
                _saveFoodState.postValue(Result.Loading)
                try {
                    val result = dashboardUseCases.saveFood(listFood)
                    _saveFoodState.postValue(result) // Langsung set nilainya


                    if (result is Result.Success) {
                        _navigateToDashboard.send(Unit) // Kirim sinyal!
                    }
                } catch (e: Exception) {
                    _saveFoodState.postValue(Result.Error(e.message ?: "Unexpected Error"))
                }
            }
        }
    }
}