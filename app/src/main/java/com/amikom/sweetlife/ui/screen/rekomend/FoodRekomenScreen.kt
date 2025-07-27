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
//    data class Loaded(val foodList: List<com.amikom.sweetlife.data.remote.dto.rekomen.>) : FoodRekomenUiState()
//    data class Failed(val error: String?) : FoodRekomenUiState()
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

    val foodRecommendations = viewModel.foodRecommendations.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
//    val foodList = foodRecommendations.value?.foodList ?: emptyList()
    val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
    val buttons = getBottomNavButtons(selectedIndex, navController)

//    val uiState = when {
//        isLoading -> FoodRekomenUiState.Loading
//        error != null -> FoodRekomenUiState.Failed(error)
//        else -> FoodRekomenUiState.Loaded(foodList)
//    }

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
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
//            when (uiState) {
//                is FoodRekomenUiState.Loading ->
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                is FoodRekomenUiState.Failed ->
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text(text = uiState.error ?: "Unknown error", style = MaterialTheme.typography.bodyLarge)
//                    }
//                is FoodRekomenUiState.Loaded ->
//                    LazyColumn(modifier = Modifier.fillMaxSize()) {
//                        items(uiState.foodList) { food ->
//                            RekomendItemFood(food)
//                        }
//                    }
//            }
        }
    }
}