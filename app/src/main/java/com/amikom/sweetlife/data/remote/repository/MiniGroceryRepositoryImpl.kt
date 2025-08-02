package com.amikom.sweetlife.data.remote.repository

import com.amikom.sweetlife.data.remote.dto.minigrocery.GroceryItemDto
import com.amikom.sweetlife.data.remote.retrofit.FeatureApiService
import com.amikom.sweetlife.domain.repository.GroceryRepository
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.minicourse.MiniCourseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MiniGroceryRepositoryImpl @Inject constructor(
    private val api: FeatureApiService
): GroceryRepository {
    override suspend fun getMiniGrocery(): Result<List<GroceryItemDto>> {
        return withContext(Dispatchers.IO) {
            var finalResult: Result<List<GroceryItemDto>>
            try {
                val response = api.getMiniGrocery()
                if (response.isSuccessful) {
                    val groceryResponse = response.body()
                    val groceryItems = groceryResponse?.data?.data
                    if (groceryItems != null) {
                        finalResult = Result.Success(groceryItems)
                    } else {
                        finalResult = Result.Error("Data grocery tidak ditemukan di response.")
                    }
                } else {
                    finalResult = Result.Error("Terjadi kesalahan server: ${response.code()}")
                }
            } catch (e: Exception) {
                finalResult = Result.Error(e.message ?: "Terjadi kesalahan jaringan atau tak terduga.")
            }
            return@withContext finalResult
        }
    }

    override suspend fun getGroceryItemById(id : Int): Result<GroceryItemDto> {
        return withContext(Dispatchers.IO) {
            when (val listResult = getMiniGrocery()) {
                is Result.Success -> {
                    val foundItem = listResult.data.find { it.id == id }
                    if (foundItem != null) {
                        Result.Success(foundItem)
                    } else {
                        Result.Error("Produk dengan ID $id tidak ditemukan.")
                    }
                }

                is Result.Error -> listResult
                else -> Result.Error("Gagal mengambil daftar produk.")
            }
        }
    }
}