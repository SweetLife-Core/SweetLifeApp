package com.amikom.sweetlife.domain.manager

import com.amikom.sweetlife.data.model.NewTokenModel
import com.amikom.sweetlife.data.model.UserModel
import kotlinx.coroutines.flow.Flow


interface LocalAuthUserManager  {

    suspend fun saveInfoLogin(userModel: UserModel)

    suspend fun saveNewTokenInfo(newToken: NewTokenModel)

    suspend fun saveNewHasHealth(hasHealth: Boolean)

    fun readHasHealth(): Flow<Boolean>

    fun readInfoLogin(): Flow<Boolean>

    suspend fun logout()

    fun getAllToken(): Flow<List<Pair<String, String?>>>
}