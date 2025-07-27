package com.amikom.sweetlife.ui.screen.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amikom.sweetlife.ui.theme.MainBlue

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val userChoice by viewModel.userChoice.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        userChoice.forEach { userChoice ->
            FoodLogItem(userChoice = userChoice, viewModel = viewModel)
        }
    }
}

@Composable
fun FoodLogItem(userChoice: UserChoice, viewModel: SettingsViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Column
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = userChoice.icon, contentDescription = "Icon")
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userChoice.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = userChoice.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Switch(
                    checked = userChoice.isEnabled, onCheckedChange = { isChecked ->
                        viewModel.updateUserChoice(userChoice.id, isChecked)
                    }
                )
            }
        }
    }
}