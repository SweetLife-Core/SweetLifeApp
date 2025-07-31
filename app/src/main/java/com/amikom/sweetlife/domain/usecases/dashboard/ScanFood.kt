package com.amikom.sweetlife.domain.usecases.dashboard

import androidx.lifecycle.LiveData
import com.amikom.sweetlife.data.model.DashboardModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.scan.ScanResponse
import com.amikom.sweetlife.domain.repository.DashboardRepository
import okhttp3.MultipartBody

class ScanFood (
    private val dashboardRepository: DashboardRepository
) {
    suspend operator fun invoke(image: MultipartBody.Part): Result<ScanResponse> {
        return dashboardRepository.scanFood(image)
    }
}