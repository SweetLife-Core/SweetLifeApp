package com.amikom.sweetlife.domain.manager

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val localAuthUserManager: LocalAuthUserManager
) {
    val isUserLoggedOut = MutableStateFlow(false)

    init {
        observeLogout()
    }

    private fun observeLogout() {
        // Cek login state
        localAuthUserManager.readInfoLogin().onEach { isLogin ->
            if (!isLogin) isUserLoggedOut.value = true
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }
}