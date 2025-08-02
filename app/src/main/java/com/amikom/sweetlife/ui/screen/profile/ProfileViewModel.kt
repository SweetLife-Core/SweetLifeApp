package com.amikom.sweetlife.ui.screen.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.model.DashboardModel
import com.amikom.sweetlife.data.model.HealthProfileModel
import com.amikom.sweetlife.data.model.ProfileModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.domain.usecases.dashboard.DashboardUseCases
import com.amikom.sweetlife.domain.usecases.profile.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _profileState = MutableLiveData<Result<ProfileModel>>()
    val profileData: LiveData<Result<ProfileModel>> = _profileState

    private val _healthState = MutableLiveData<Result<HealthProfileModel>>()
    val healthData: LiveData<Result<HealthProfileModel>> = _healthState

    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    init {
        viewModelScope.launch {
            authUseCases.checkIsUserLogin().collect { isLoggedIn ->
                _isUserLoggedIn.value = isLoggedIn
            }
        }
        fetchProfileData()
        fetchHealthData()
    }

    private fun fetchHealthData() {
        viewModelScope.launch {
            _healthState.postValue(Result.Loading)

            try {
                val result = profileUseCases.fetchDataHealthProfile()

                result.observeForever { healthResult ->
                    _healthState.postValue(healthResult)
                }
            } catch (e: Exception) {
                _healthState.postValue(Result.Error(e.message ?: "Unexpected Error"))
            }
        }
    }

    private fun fetchProfileData() {
        viewModelScope.launch {
            _profileState.postValue(Result.Loading)

            try {
                val result = profileUseCases.fetchDataProfile()
                Log.d("ProfileViewModel", "fetchProfileData: ${result}")

                result.observeForever { profileResult ->
                    _profileState.postValue(profileResult)
                }
            } catch (e: Exception) {
                _profileState.postValue(Result.Error(e.message ?: "Unexpected Error"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = authUseCases.logout()
            authUseCases.logout()
        }
    }
}
