package com.amikom.sweetlife.data.manager

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.amikom.sweetlife.data.model.NewTokenModel
import com.amikom.sweetlife.data.model.UserModel
import com.amikom.sweetlife.domain.manager.LocalAuthUserManager
import com.amikom.sweetlife.util.Constants
import com.amikom.sweetlife.util.Constants.LOCAL_USER_INFO
import com.amikom.sweetlife.util.Constants.USER_REFRESH_TOKEN
import com.amikom.sweetlife.util.Constants.USER_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalAuthUserManagerImpl @Inject constructor(
    private val context: Context
) : LocalAuthUserManager {
    override suspend fun saveInfoLogin(userModel: UserModel) {
        Log.d("LocalAuthUserManagerImpl", "Saving user info: ${userModel.name}, ID: ${userModel.id}")
        context.dataStore.edit { pref ->
            pref[LocalUserInfoKeys.USER_EMAIL] = userModel.email
            pref[LocalUserInfoKeys.USER_ID] = userModel.id
            pref[LocalUserInfoKeys.USER_NAME] = userModel.name
            pref[LocalUserInfoKeys.USER_GENDER] = userModel.gender
            pref[LocalUserInfoKeys.USER_TOKEN] = userModel.token
            pref[LocalUserInfoKeys.USER_REFRESH_TOKEN] = userModel.refreshToken
            pref[LocalUserInfoKeys.USER_IS_LOGIN] = userModel.isLogin
            pref[LocalUserInfoKeys.USER_HAS_HEALTH_PROFILE] = userModel.hasHealthProfile
        }
    }

    override suspend fun saveNewTokenInfo(newToken: NewTokenModel) {
        context.dataStore.edit { pref ->
            pref[LocalUserInfoKeys.USER_TOKEN] = newToken.accessToken
            pref[LocalUserInfoKeys.USER_REFRESH_TOKEN] = newToken.refreshToken
        }
    }

    override suspend fun saveNewHasHealth(hasHealth: Boolean) {
        context.dataStore.edit { pref ->
            pref[LocalUserInfoKeys.USER_HAS_HEALTH_PROFILE] = hasHealth
        }
    }

    override fun readHasHealth(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LocalUserInfoKeys.USER_HAS_HEALTH_PROFILE] ?: false
        }
    }

    override fun readInfoLogin(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LocalUserInfoKeys.USER_IS_LOGIN] ?: false
        }
    }

    override suspend fun logout() {
        context.dataStore.edit { pref ->
            pref.clear()
        }
    }

    override fun getAllToken(): Flow<List<Pair<String, String?>>> {
        return context.dataStore.data.map { pref ->
            listOf(
                USER_TOKEN to pref[LocalUserInfoKeys.USER_TOKEN],
                USER_REFRESH_TOKEN to pref[LocalUserInfoKeys.USER_REFRESH_TOKEN],
            )
        }
    }

    override suspend fun readUserId(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[LocalUserInfoKeys.USER_ID]
    }

}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = LOCAL_USER_INFO)

private object LocalUserInfoKeys {
    val USER_ID = stringPreferencesKey(name = Constants.USER_ID)
    val USER_EMAIL = stringPreferencesKey(name = Constants.USER_EMAIL)
    val USER_NAME = stringPreferencesKey(name = Constants.USER_NAME)
    val USER_GENDER = stringPreferencesKey(name = Constants.USER_GENDER)
    val USER_TOKEN = stringPreferencesKey(name = Constants.USER_TOKEN)
    val USER_REFRESH_TOKEN = stringPreferencesKey(name = Constants.USER_REFRESH_TOKEN)
    val USER_IS_LOGIN = booleanPreferencesKey(name = Constants.USER_IS_LOGIN)
    val USER_HAS_HEALTH_PROFILE = booleanPreferencesKey(name = Constants.USER_HAS_HEALTH_PROFILE)
}