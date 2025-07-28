package com.amikom.sweetlife.ui.screen.History

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amikom.sweetlife.data.remote.dto.HistoryResponse.FoodLog
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.getBottomNavButtons
import com.amikom.sweetlife.util.Constants

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavController
) {
    val foodLogs by viewModel.foodHistory.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
    val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
    val buttons = getBottomNavButtons(selectedIndex, navController)

    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                buttons = buttons,
                navController = navController,
                currentScreen = Route.HistoryScreen
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) {  fillMaxSize ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(
                    text = error ?: "An unknown error occurred.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )

                foodLogs?.isEmpty() == true -> Text(
                    text = "No food logs available.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp),
                ) {
                    item {
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp, 32.dp)
                        )
                    }
                    items(foodLogs.orEmpty()) { foodHistory -> // Loop over FoodHistory
                        foodHistory.entries.reversed().forEach { foodLog -> // Loop over FoodLog entries inside each FoodHistory
                            FoodLogItem(foodLog = foodLog)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodLogItem(foodLog: FoodLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Food Details Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = foodLog.foodName.ifEmpty { "Unknown Food" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = foodLog.time.ifEmpty { "Unknown Time" },
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            // Calories Column
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${foodLog.calories?.toString() ?: "Unknown"} kcal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewFoodLogItem() {

    FoodLogItem(
        foodLog = FoodLog(
            id = "1",
            foodName = "Nasi Goreng",
            calories = 500,
            time = "12:00",
            totalUnits = 1
        )
    )
    FoodLogItem(
        foodLog = FoodLog(
            id = "1",
            foodName = "Nasi Goreng",
            calories = 500,
            time = "12:00",
            totalUnits = 1
        )
    )
}
