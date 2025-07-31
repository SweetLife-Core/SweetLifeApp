package com.amikom.sweetlife.ui.screen.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.model.UserModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _loginResult = MutableStateFlow<Result<UserModel>>(Result.Empty)
    val loginResult: StateFlow<Result<UserModel>> = _loginResult

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _isUserHasHealth = MutableStateFlow(false)
    val isUserHasHealth: StateFlow<Boolean> = _isUserHasHealth

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginProcess -> {
                viewModelScope.launch {
                    loginProcess(event.email, event.password)
                }
            }
        }
    }

    private suspend fun loginProcess(email: String, password: String) {
        _loginResult.value = Result.Loading
        authUseCases.login(email, password)
            .asFlow()
            .collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        authUseCases.saveUserInfoLogin(result.data)
                        authUseCases.checkIsUserLogin().collect { isLoggedIn ->
                            _isUserLoggedIn.value = isLoggedIn
                            if (isLoggedIn) {
                                _isUserHasHealth.value = result.data.hasHealthProfile
                                _loginResult.value = result
                            }
                        }
                    }
                    else -> _loginResult.value = result
                }
            }
    }

}