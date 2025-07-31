package com.amikom.sweetlife.domain.usecases.auth

import androidx.lifecycle.LiveData
import com.amikom.sweetlife.data.model.ForgotPasswordModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.manager.LocalAuthUserManager
import com.amikom.sweetlife.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

// ForgotUserPassword.kt
class ForgotUserPassword(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Flow<Result<ForgotPasswordModel>> {
        return authRepository.forgotPassword(email)
    }
}
