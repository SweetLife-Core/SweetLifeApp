package com.amikom.sweetlife.domain.repository

import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.minicourse.MiniCourseDto
import com.amikom.sweetlife.data.remote.dto.minigrocery.GroceryItemDto

interface GroceryRepository {
    suspend fun getMiniGrocery(): Result<List<GroceryItemDto>>
    suspend fun getGroceryItemById(id: Int): Result<GroceryItemDto>
}