package com.amikom.sweetlife.domain.manager

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    val isUserLoggedOut = sessionManager.isUserLoggedOut
    val userId = sessionManager.userId
}
