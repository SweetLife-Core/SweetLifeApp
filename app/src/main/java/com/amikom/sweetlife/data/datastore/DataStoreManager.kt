package com.amikom.sweetlife.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.amikom.sweetlife.util.Constants.USER_SETTINGS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val HAS_HEALTH_PROFILE = booleanPreferencesKey("has_health_profile")
    }

    suspend fun setIsUserLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun clearAccessToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
        }
    }

    suspend fun clearRefreshToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.REFRESH_TOKEN)
        }
    }

    suspend fun setHasHealthProfile(has: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HAS_HEALTH_PROFILE] = has
        }
    }

    fun readIsLoggedIn(): Flow<Boolean> = context.dataStore.data.map {
        it[Keys.IS_LOGGED_IN] ?: false
    }

    fun readAccessToken(): Flow<String?> = context.dataStore.data.map {
        it[Keys.ACCESS_TOKEN]
    }

    fun readRefreshToken(): Flow<String?> = context.dataStore.data.map {
        it[Keys.REFRESH_TOKEN]
    }
}
