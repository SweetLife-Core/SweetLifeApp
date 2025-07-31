package com.amikom.sweetlife.domain.usecases.dashboard

import androidx.lifecycle.LiveData
import com.amikom.sweetlife.data.model.DashboardModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.repository.DashboardRepository

class FetchData (
    private val dashboardRepository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardModel> {
        return dashboardRepository.fetchDataDashboard()
    }
}