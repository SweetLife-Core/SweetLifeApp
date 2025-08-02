package com.amikom.sweetlife.data.manager

import android.content.Context
import com.amikom.sweetlife.data.datastore.dataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.amikom.sweetlife.domain.manager.LocalUserManager
import com.amikom.sweetlife.util.Constants
import com.amikom.sweetlife.util.Constants.USER_SETTINGS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserManagerImpl(
    private val context: Context
) : LocalUserManager {
    override suspend fun saveAppEntry() {
        context.dataStore.edit { settings ->
            settings[UserSettingsKeys.APP_ENTRY] = true
        }
    }

    override suspend fun updateAppThemeMode(isDarkMode: Boolean) {
        context.dataStore.edit { settings ->
            settings[UserSettingsKeys.APP_IS_DARK_MODE] = isDarkMode
        }
    }

    override fun readAppEntry(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[UserSettingsKeys.APP_ENTRY] ?: false
        }
    }

    override fun getAppThemeMode(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[UserSettingsKeys.APP_IS_DARK_MODE] ?: false
        }
    }
}


private object UserSettingsKeys {
    val APP_ENTRY = booleanPreferencesKey(name =  Constants.APP_ENTRY)
    val APP_IS_DARK_MODE = booleanPreferencesKey(name =  Constants.APP_IS_DARK_MODE) // true = dark
}