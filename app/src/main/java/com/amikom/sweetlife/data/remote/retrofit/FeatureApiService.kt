package com.amikom.sweetlife.data.remote.retrofit

import com.amikom.sweetlife.data.model.FoodRequest
import com.amikom.sweetlife.data.remote.dto.EditHealth.EditHealthResponse
import com.amikom.sweetlife.data.remote.dto.HistoryResponse.HistoryResponse
import com.amikom.sweetlife.data.remote.dto.dashboard.DashboardResponse
import com.amikom.sweetlife.data.remote.dto.health_profile.CreateHealthResponse
import com.amikom.sweetlife.data.remote.dto.health_profile.HealthProfileResponse
import com.amikom.sweetlife.data.remote.dto.profile.ProfileFotoResponse
import com.amikom.sweetlife.data.remote.dto.profile.ProfileResponse
import com.amikom.sweetlife.data.remote.dto.profile.UpdateProfileResponse
import com.amikom.sweetlife.data.remote.dto.rekomen.RekomenResponse
import com.amikom.sweetlife.data.remote.dto.scan.FindFoodResponse
import com.amikom.sweetlife.data.remote.dto.scan.SaveFoodResponse
import com.amikom.sweetlife.data.remote.dto.scan.ScanResponse
import com.amikom.sweetlife.data.remote.json_request.EditHealthRequest
import com.amikom.sweetlife.data.remote.json_request.FindFoodRequest
import com.amikom.sweetlife.data.remote.json_request.HealthRequest
import com.amikom.sweetlife.data.remote.json_request.ProfileRequest
import com.amikom.sweetlife.util.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface FeatureApiService {

    @GET("${Constants.API_VERSION}users/dashboard")
    suspend fun dashboard(): Response<DashboardResponse>

    @GET("${Constants.API_VERSION}users/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("${Constants.API_VERSION}users/profile")
    suspend fun updateProfile(@Body profile: ProfileRequest): Response<ProfileResponse>

    @POST("${Constants.API_VERSION}users/health")
    suspend fun createHealth(@Body health: HealthRequest): Response<CreateHealthResponse>

    @POST("${Constants.API_VERSION}food/find")
    suspend fun findFood(@Body find: FindFoodRequest): Response<FindFoodResponse>

    @POST("${Constants.API_VERSION}food/save")
    suspend fun saveFood(@Body save: FoodRequest): Response<SaveFoodResponse>

    @GET("${Constants.API_VERSION}users/health")
    suspend fun getHealth(): Response<HealthProfileResponse>

    @GET("${Constants.API_VERSION}food-recomendation")
    suspend fun getRekomendasi(): Response<RekomenResponse>

    @PUT("${Constants.API_VERSION}users/health")
    suspend fun updateHealth(@Body health: EditHealthRequest): Response<EditHealthResponse>

    @GET("${Constants.API_VERSION}users/history")
    suspend fun getHistory(): Response<HistoryResponse>

    @Multipart
    @PUT("${Constants.API_VERSION}users/profile/image")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): ProfileFotoResponse

    @Multipart
    @POST("${Constants.API_VERSION}food/scan")
    suspend fun foodScan(@Part image: MultipartBody.Part): Response<ScanResponse>

//    @Multipart
//    @POST("upload")
//    suspend fun foodScan(@Part image: MultipartBody.Part): Response<ScanResponse>

    @Multipart
    @PUT("${Constants.API_VERSION}users/profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("date_of_birth") dateOfBirth: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profilePicture: MultipartBody.Part? = null
    ): Response<UpdateProfileResponse>
}