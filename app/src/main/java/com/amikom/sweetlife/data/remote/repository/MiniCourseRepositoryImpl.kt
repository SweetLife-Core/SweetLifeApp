package com.amikom.sweetlife.data.remote.repository

import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.ErrorResponse // Asumsi ada ErrorResponse
import com.amikom.sweetlife.data.remote.dto.minicourse.MiniCourseDto
import com.amikom.sweetlife.data.remote.dto.minicourse.MiniCourseResponse
import com.amikom.sweetlife.data.remote.retrofit.FeatureApiService
import com.amikom.sweetlife.domain.repository.MiniCourseRepository
import com.google.gson.Gson
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MiniCourseRepositoryImpl @Inject constructor(
    private val api: FeatureApiService
) : MiniCourseRepository {
    override suspend fun getMiniCourses(): Result<List<MiniCourseDto>> {
        return withContext(Dispatchers.IO) {
            var finalResult: Result<List<MiniCourseDto>>
            try {
                val response = api.getMiniCourses()
                if (response.isSuccessful) {
                    val miniCourseResponse = response.body()
                    val courses = miniCourseResponse?.data?.data
                    if (courses != null) {
                        finalResult = Result.Success(courses)
                    } else {
                        finalResult = Result.Error("Data mini course tidak ditemukan di response.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    finalResult = Result.Error(errorResponse?.message ?: "Terjadi kesalahan server: ${response.code()}")
                }
            } catch (e: Exception) {
                finalResult = Result.Error(e.message ?: "Terjadi kesalahan jaringan atau tak terduga.")
            }
            finalResult
        }
    }
}