package com.amikom.sweetlife.data.remote.retrofit

import com.amikom.sweetlife.data.remote.dto.forgot_password.ForgotPasswordResponse
import com.amikom.sweetlife.data.remote.dto.login.LoginResponse
import com.amikom.sweetlife.data.remote.dto.logout.LogoutResponse
import com.amikom.sweetlife.data.remote.dto.refresh_token.RefreshTokenResponse
import com.amikom.sweetlife.data.remote.dto.register.RegisterResponse
import com.amikom.sweetlife.data.remote.json_request.ForgotPasswordRequest
import com.amikom.sweetlife.data.remote.json_request.LoginRequest
import com.amikom.sweetlife.data.remote.json_request.RefreshTokenRequest
import com.amikom.sweetlife.data.remote.json_request.RegisterRequest
import com.amikom.sweetlife.ui.screen.History.ApiResponse
import com.amikom.sweetlife.util.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("${Constants.API_VERSION}auth/login")
    suspend fun login(

        @Body request: LoginRequest

    ) : Response<LoginResponse>

    @POST("${Constants.API_VERSION}auth/register")
    suspend fun register(

        @Body request: RegisterRequest

    ) : Response<RegisterResponse>

    @POST("${Constants.API_VERSION}auth/forgot-password")
    suspend fun forgotPassword(

        @Body request: ForgotPasswordRequest

    ) : Response<ForgotPasswordResponse>

    @POST("${Constants.API_VERSION}auth/refresh-token")
    suspend fun refreshToken(

        @Body request: RefreshTokenRequest

    ) : Response<RefreshTokenResponse>

    @POST("${Constants.API_VERSION}auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse>


}