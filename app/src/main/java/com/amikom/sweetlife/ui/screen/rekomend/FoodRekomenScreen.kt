package com.amikom.sweetlife.ui.screen.rekomend


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.getBottomNavButtons
import com.amikom.sweetlife.util.Constants
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


sealed class FoodRekomenUiState {
    object Loading : FoodRekomenUiState()
    data class Loaded(val foodList: List<com.amikom.sweetlife.data.remote.dto.rekomen.FoodRecommendation>) : FoodRekomenUiState()
    data class Failed(val error: String?) : FoodRekomenUiState()
}

@Composable
fun FoodRekomenScreen(
    viewModel: RekomenViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.fetchRekomend()
        Log.d("FoodRekomenScreen", "Fetching food recommendations")
    }

    val foodRecommendations by viewModel.foodRecommendations.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
    val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
    val buttons = getBottomNavButtons(selectedIndex, navController)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                buttons = buttons,
                navController = navController,
                currentScreen = Route.FoodRekomenScreen
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = error ?: "Unknown error", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(foodRecommendations) { food ->
                            RekomendItemFood(item = food)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
