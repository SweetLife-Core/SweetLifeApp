package com.amikom.sweetlife.data.remote.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.amikom.sweetlife.data.model.DiabetesPrediction
import com.amikom.sweetlife.data.model.HealthProfileModel
import com.amikom.sweetlife.data.model.ProfileModel
import com.amikom.sweetlife.data.model.UpdateProfileModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.ErrorResponse
import com.amikom.sweetlife.data.remote.json_request.HealthRequest
import com.amikom.sweetlife.data.remote.retrofit.FeatureApiService
import com.amikom.sweetlife.domain.repository.ProfileRepository
import com.amikom.sweetlife.util.AppExecutors
import com.amikom.sweetlife.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val featureApiService: FeatureApiService,
    private val appExecutors: AppExecutors
) : ProfileRepository {
    private val defaultErrorValue = Constants.DEFAULT_ERROR_TEXT

    override suspend fun fetchDataProfile(): LiveData<Result<ProfileModel>> {
        val result = MediatorLiveData<Result<ProfileModel>>()
        result.value = Result.Loading

        try {
            // Perform API call
            val response = featureApiService.getProfile()

            if (response.isSuccessful) {
                // Parse response body
                val rawData = response.body()
                val data = response.body()?.data

                val profileModel = ProfileModel(
                    id = data?.id ?: "",
                    image = data?.photoProfile ?: defaultErrorValue,
                    email = data?.email ?: defaultErrorValue,
                    name = data?.name ?: defaultErrorValue,
                    gender = data?.gender ?: defaultErrorValue,
                    dateOfBirth = data?.dateOfBirth ?: defaultErrorValue
                )

                // Update result on main thread
                appExecutors.mainThread.execute {
                    result.value = Result.Success(profileModel)
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

    override suspend fun fetchHealthProfile(): LiveData<Result<HealthProfileModel>> {
        val result = MediatorLiveData<Result<HealthProfileModel>>()
        result.value = Result.Loading

        try {
            // Perform API call
            val response = featureApiService.getHealth()

            if (response.isSuccessful) {
                // Parse response body
                val rawData = response.body()
                val data = response.body()?.data

                val healthProfileModel = HealthProfileModel(
                    userId = data?.userId ?: "",
                    height = data?.height ?: 0.0,
                    weight = data?.weight ?: 0.0,
                    isDiabetic = data?.isDiabetic ?: false,
                    diabeticType = data?.diabeticType ?: defaultErrorValue,
                    insulinLevel = data?.insulinLevel ?: 0,
                    bloodPressure = data?.bloodPressure ?: 0,
                    smokingHistory = data?.smokingHistory ?: defaultErrorValue,
                    hasHeartDisease = data?.hasHeartDisease ?: false,
                    activityLevel = data?.activityLevel ?: defaultErrorValue,
                    diabetesPrediction = DiabetesPrediction(
                        riskPercentage = data?.diabetesPrediction?.riskPercentage ?: 0.0,
                        riskLevel = data?.diabetesPrediction?.riskLevel ?: defaultErrorValue,
                        note = data?.diabetesPrediction?.note ?: defaultErrorValue
                    )
                )

                // Update result on main thread
                appExecutors.mainThread.execute {
                    result.value = Result.Success(healthProfileModel)
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

    override suspend fun updateDataProfile(dataProfile: UpdateProfileModel): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)

        try {
            val name = dataProfile.name.toRequestBody("text/plain".toMediaType())
            val dateOfBirth = dataProfile.dateOfBirth.toRequestBody("text/plain".toMediaType())
            val gender = dataProfile.gender.toRequestBody("text/plain".toMediaType())

            val profilePicturePart = dataProfile.profilePicture?.let { bitmap ->
                val file = File(context.cacheDir, "profile_picture.jpg").apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        OutputStream.nullOutputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                    }
                }
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }

            // Panggil API
            val response = featureApiService.updateProfile(
                name = name,
                dateOfBirth = dateOfBirth,
                gender = gender,
                profilePicture = profilePicturePart
            )

            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Result.Error(errorBody ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override suspend fun createHealthProfile(dataHealth: HealthRequest): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)

        try {
            // Panggil API
            val response = featureApiService.createHealth(dataHealth)

            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Result.Error(errorBody ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}