package com.amikom.sweetlife.domain.repository

import com.amikom.sweetlife.data.remote.dto.minicourse.MiniCourseDto
import com.amikom.sweetlife.data.remote.Result
import javax.inject.Inject

interface MiniCourseRepository{
    suspend fun getMiniCourses(): Result<List<MiniCourseDto>>
}