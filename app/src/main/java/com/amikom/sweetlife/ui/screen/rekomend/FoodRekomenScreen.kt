package com.amikom.sweetlife.ui.screen.rekomend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun FoodRekomenScreen(
    viewModel = RekomenViewModel = hiltViewModel(),
    navController = navController) {
    // Implement the UI for food recommendations here
    // You can use LazyColumn to display a list of food recommendations
    // and handle navigation using navController as needed.

    // Example placeholder content:
    Box(modifier = androidx.compose.ui.Modifier.Companion.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text = "Food Recommendations will be displayed here", style = MaterialTheme.typography.bodyLarge)
    }
}