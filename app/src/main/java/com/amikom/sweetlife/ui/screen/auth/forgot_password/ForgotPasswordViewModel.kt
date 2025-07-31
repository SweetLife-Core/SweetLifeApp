package com.amikom.sweetlife.ui.screen.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.model.ForgotPasswordModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _forgotPasswordResult = MutableStateFlow<Result<ForgotPasswordModel>>(Result.Empty)
    val forgotPasswordResult: StateFlow<Result<ForgotPasswordModel>> = _forgotPasswordResult

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.ForgotPassword -> {
                forgotPassword(event.email)
            }
        }
    }

    private fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordResult.value = Result.Loading
            authUseCases.forgotPassword(email).collect { result ->
                _forgotPasswordResult.value = result
            }
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            authUseCases.checkIsUserLogin().collect { isLoggedIn ->
                _isUserLoggedIn.value = isLoggedIn
            }
        }
    }

    fun resetForgotPasswordResult() {
        _forgotPasswordResult.value = Result.Empty
    }

}