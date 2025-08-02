package com.amikom.sweetlife.data.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.amikom.sweetlife.util.Constants.USER_SETTINGS

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTINGS)
