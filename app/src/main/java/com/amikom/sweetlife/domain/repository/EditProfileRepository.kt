package com.amikom.sweetlife.domain.repository

import androidx.lifecycle.LiveData
import com.amikom.sweetlife.data.model.ProfileModel
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.data.remote.dto.profile.ProfileFotoResponse
import com.amikom.sweetlife.data.remote.dto.profile.ProfileResponse
import com.amikom.sweetlife.data.remote.json_request.ProfileRequest
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface EditProfileRepository {
    suspend fun updateProfile(profile: ProfileRequest): ProfileResponse
    suspend fun uploadProfileImage(imagePart: MultipartBody.Part): ProfileFotoResponse
}