package com.amikom.sweetlife.ui.screen.profile.settings

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.domain.usecases.app_entry.AppEntryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appEntryUseCases: AppEntryUseCases
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(true) // default true for dark mode
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _userChoice = MutableStateFlow<List<UserChoice>>(emptyList())
    val userChoice = _userChoice.asStateFlow()

    init {
        // Initialize theme mode from storage
        viewModelScope.launch {
            appEntryUseCases.getAppThemeMode().collect { isDarkModeValue ->
                _isDarkMode.value = isDarkModeValue
                updateUserChoiceForDarkMode(isDarkModeValue)
            }
        }
    }

    private fun updateUserChoiceForDarkMode(isDarkMode: Boolean) {
        _userChoice.value = listOf(
            UserChoice(1, "Notification", "Manage notifications settings", Icons.Default.Notifications, true),
            UserChoice(2, "Dark Mode", "Switch between light and dark themes", Icons.Default.Face, isDarkMode)
        )
    }

    fun updateUserChoice(id: Int, isEnabled: Boolean) {
        _userChoice.value = _userChoice.value.map {
            if (it.id == id) it.copy(isEnabled = isEnabled) else it
        }

        if (id == 2) { // Dark Mode
            viewModelScope.launch {
                appEntryUseCases.updateAppThemeMode(isEnabled)
                _isDarkMode.value = isEnabled
            }
        }
    }
}

data class UserChoice(
    val id: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isEnabled: Boolean
)