package com.amikom.sweetlife.ui.screen.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.model.DailyProgress
import com.amikom.sweetlife.data.model.DashboardModel
import com.amikom.sweetlife.data.model.Data
import com.amikom.sweetlife.data.model.ProgressDetail
import com.amikom.sweetlife.data.model.Status
import com.amikom.sweetlife.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.domain.usecases.dashboard.DashboardUseCases
import com.amikom.sweetlife.domain.usecases.profile.ProfileUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardUseCases: DashboardUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _dashboardState = MutableStateFlow<Result<DashboardModel>>(Result.Loading)
    val dashboardState = _dashboardState.asStateFlow()

    init {
        viewModelScope.launch {
            authUseCases.checkIsUserLogin().collect { isLoggedIn ->
                _isUserLoggedIn.value = isLoggedIn
            }
        }
        fetchDashboard()
    }

    private fun fetchDashboard() {
        viewModelScope.launch {
            try {
                val result = dashboardUseCases.fetchData.invoke()
                if (result is Result.Success) {
                    _dashboardState.value = result
                } else {
                    Log.e("DashboardViewModel", "fetchDashboard: ${result}")
//                    fetchMockDashboard()
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "fetchDashboard: ${e.message}")
//                _dashboardState.value = Result.Error(e.message ?: "Unexpected Error")
//                fetchMockDashboard()
            }
        }
    }


    private fun fetchMockDashboard() {
        val mockdata = DashboardModel(
            status = true,
            data = Data(
                dailyProgress = DailyProgress(
                    calories = ProgressDetail(
                        current = 100.0,
                        percent = 50.0,
                        satisfaction = "Good",
                        target = 200.0
                    ),
                    carbs = ProgressDetail(
                        current = 100.0,
                        percent = 50.0,
                        satisfaction = "Good",
                        target = 200.0
                    ),
                    sugar = ProgressDetail(
                        current = 100.0,
                        percent = 50.0,
                        satisfaction = "Good",
                        target = 200.0
                    )
                ),
                status = Status(
                    message = "Good",
                    satisfaction = "Good"
                ),
                user = User(
                    name = "John Doe",
                    diabetes = true,
                    diabetesType = "Type 1"
                )
            )
        )
        _dashboardState.value = Result.Success(mockdata)
    }
}
