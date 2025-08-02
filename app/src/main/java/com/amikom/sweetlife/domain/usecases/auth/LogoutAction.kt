package com.amikom.sweetlife.domain.usecases.auth

import androidx.lifecycle.LiveData
import com.amikom.sweetlife.data.model.UserModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LogoutAction(
    private val authRepository: AuthRepository,
    private val localAuthUserManager: com.amikom.sweetlife.domain.manager.LocalAuthUserManager
) {

    suspend operator fun invoke(): LiveData<Result<Boolean>> {
        val result = authRepository.logout()
        localAuthUserManager.clearUserData()
        return result
    }
}