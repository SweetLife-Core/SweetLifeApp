package com.amikom.sweetlife.domain.repository

import androidx.lifecycle.LiveData
import com.amikom.sweetlife.data.model.ForgotPasswordModel
import com.amikom.sweetlife.data.model.NewTokenModel
import com.amikom.sweetlife.data.model.UserModel
import com.amikom.sweetlife.data.remote.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email: String, password: String): LiveData<Result<UserModel>>

    suspend fun register(email: String, password: String) : LiveData<Result<Boolean>>

    suspend fun changePassword(oldPassword: String, newPassword: String)

    fun forgotPassword(email: String): Flow<Result<ForgotPasswordModel>>

    suspend fun logout() : LiveData<Result<Boolean>>

    suspend fun refreshToken(refreshToken: String) : Result<NewTokenModel>

}