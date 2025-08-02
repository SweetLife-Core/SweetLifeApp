package com.amikom.sweetlife.data.remote.repository

import android.R.id.message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.amikom.sweetlife.data.model.ForgotPasswordModel
import com.amikom.sweetlife.data.model.NewTokenModel
import com.amikom.sweetlife.data.model.UserModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.ErrorResponse
import com.amikom.sweetlife.data.remote.json_request.ForgotPasswordRequest
import com.amikom.sweetlife.data.remote.json_request.LoginRequest
import com.amikom.sweetlife.data.remote.json_request.RefreshTokenRequest
import com.amikom.sweetlife.data.remote.json_request.RegisterRequest
import com.amikom.sweetlife.data.remote.retrofit.AuthApiService
import com.amikom.sweetlife.domain.manager.LocalAuthUserManager
import com.amikom.sweetlife.domain.repository.AuthRepository
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.util.AppExecutors
import com.amikom.sweetlife.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val appExecutors: AppExecutors,
    private val localAuthUserManager: LocalAuthUserManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): LiveData<Result<UserModel>> {
        val result = MediatorLiveData<Result<UserModel>>()
        result.value = Result.Loading

        try {
            // Create request
            val loginRequest = LoginRequest(email = email, password = password)

            // Perform API call
            val response = authApiService.login(loginRequest)

            if (response.isSuccessful) {
                // Parse response body
                val loginUser = response.body()?.data
                val token = loginUser?.token ?: throw Exception("Data Token is null")
                val user = UserModel(
                    email = loginUser.user?.email ?: "",
                    name = loginUser.user?.name ?: "",
                    gender = loginUser.user?.gender ?: "",
                    token = token.accessToken ?: throw Exception("Token is null"),
                    refreshToken = token.refreshToken ?: throw Exception("Refresh Token is null"),
                    isLogin = true,
                    hasHealthProfile = loginUser.user?.hasHealthProfile ?: false,
                    id = loginUser.user?.id ?: throw Exception("User ID is null")
                )
                // Update result on main thread
                appExecutors.mainThread.execute {
                    result.value = Result.Success(user)
                }
            } else {
                // Handle error response
                val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                val message = errorBody?.error ?: response.message()
                appExecutors.mainThread.execute {
                    result.value = Result.Error(message)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
            appExecutors.mainThread.execute {
                result.value = e.message?.let { Result.Error(it) }
            }
        }

        return result
    }

    override suspend fun register(
        email: String,
        password: String
    ): LiveData<Result<Boolean>> {
        val result = MediatorLiveData<Result<Boolean>>()
        result.value = Result.Loading

        try {
            // Create request
            val registerRequest = RegisterRequest(email = email, password = password)

            // Perform API call
            val response = authApiService.register(registerRequest)

            if (response.isSuccessful) {
                // Parse response body
                val registerStatus = response.body()?.status ?: false
                val messageBody = response.body()?.message ?: "Server Error"

                if(registerStatus && messageBody == "action success") {
                    // Update result on main thread
                    appExecutors.mainThread.execute {
                        result.value = Result.Success(registerStatus)
                    }
                } else {
                    throw Exception(messageBody)
                }
            } else {
                // Handle error response
                val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                val message = errorBody?.error ?: response.message()
                appExecutors.mainThread.execute {
                    result.value = Result.Error(message)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
            appExecutors.mainThread.execute {
                result.value = e.message?.let { Result.Error(it) }
            }
        }

        return result
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun forgotPassword(email: String): Flow<Result<ForgotPasswordModel>> = flow {
        emit(Result.Loading)
        try {
            val request = ForgotPasswordRequest(email)
            val response = authApiService.forgotPassword(request)

            if (response.isSuccessful && response.body()?.status == true) {
                val data = response.body()?.data
                val model = ForgotPasswordModel(
                    email = data?.email ?: "",
                    expire = data?.expire ?: ""
                )
                emit(Result.Success(model))
            } else {
                val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                val message = errorBody?.error ?: response.message()
                emit(Result.Error(message))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unexpected error"))
        }
    }


    private var isLoggingOut = false

    override suspend fun logout(): LiveData<Result<Boolean>> {
        val result = MediatorLiveData<Result<Boolean>>()

        if (isLoggingOut) {
            result.value = Result.Error("Logout is already in progress.")
            return result
        }

        isLoggingOut = true
        result.value = Result.Loading

        try {
            val token = "Bearer ${localAuthUserManager.getAllToken()
                .firstOrNull()
                ?.find { it.first == Constants.USER_TOKEN }
                ?.second.orEmpty()}"

            if (token == "Bearer ") {
                result.value = Result.Error("Access token not found.")
                return result
            }

            val response = authApiService.logout(token)

            if (response.isSuccessful) {
                val status = response.body()?.status ?: false
                if (status == true) {
                    Log.d("AuthRepositoryImpl", "Logout successful")
                    result.value = Result.Success(true)
                } else {
                    result.value = Result.Error("Logout failed from server.")
                }
                // Selalu hapus token lokal setelah response diterima (sukses/gagal)
                localAuthUserManager.logout()
            } else {
                val error = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                val message = error?.error ?: response.message()
                result.value = Result.Error(message)
                // Selalu hapus token lokal setelah response diterima (sukses/gagal)
                localAuthUserManager.logout()
            }

        } catch (e: Exception) {
            result.value = Result.Error(e.message ?: "Unexpected error")
        } finally {
            isLoggingOut = false
        }

        return result
    }



    override suspend fun refreshToken(refreshToken: String): Result<NewTokenModel> {
        return try {
            val refreshTokenRequest = RefreshTokenRequest(refresh_token = refreshToken)

            val response = authApiService.refreshToken(refreshTokenRequest)

            if (response.isSuccessful) {
                val mainResponse = response.body()
                val dataResponse = response.body()?.data

                if (mainResponse?.status == true && mainResponse.message == "action success") {
                    Result.Success(
                        NewTokenModel(
                            accessToken = dataResponse?.accessToken.orEmpty(),
                            refreshToken = dataResponse?.refreshToken.orEmpty(),
                            type = dataResponse?.type.orEmpty()
                        )
                    )
                } else {
                    Result.Error(mainResponse?.message ?: "Unknown error")
                }
            } else {
                val errorBody = Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                Result.Error(errorBody?.error ?: response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unexpected error occurred")
        }
    }
}
