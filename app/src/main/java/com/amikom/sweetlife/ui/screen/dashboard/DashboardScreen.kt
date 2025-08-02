package com.amikom.sweetlife.ui.screen.dashboard

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.amikom.sweetlife.R
import com.amikom.sweetlife.data.model.DashboardModel
import com.amikom.sweetlife.data.model.Data
import com.amikom.sweetlife.data.model.ProgressDetail
import com.amikom.sweetlife.data.remote.Result
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.component.BottomNavigationBar
import com.amikom.sweetlife.ui.component.CustomDialog
import com.amikom.sweetlife.ui.component.getBottomNavButtons
import com.amikom.sweetlife.ui.component.rememberSelectedIndex
import com.amikom.sweetlife.ui.screen.rekomend.RekomenViewModel
import com.amikom.sweetlife.ui.screen.rekomend.RekomendItemExec
import com.amikom.sweetlife.ui.screen.rekomend.RekomendItemFood
import com.amikom.sweetlife.util.Constants
import java.util.Locale
import kotlin.math.ceil
import me.rmyhal.contentment.Contentment

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Loaded(val data: DashboardModel) : DashboardUiState()
    data class Failed(val error: String?) : DashboardUiState()
}

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val dashboardData by viewModel.dashboardState.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    if (!isUserLoggedIn) {
        CustomDialog(
            icon = R.drawable.baseline_info_outline_24,
            title = "Info",
            message = "Your session is ended. Please login again",
            openDialogCustom = remember { mutableStateOf(true) },
            buttons = listOf(
                "Ok" to {
                    navController.navigate(Route.LoginScreen) {
                        launchSingleTop = true
                    }
                }
            ),
            dismissOnBackdropClick = false
        )
    }

    val uiState = when {
        dashboardData is Result.Loading -> DashboardUiState.Loading
        dashboardData is Result.Error -> DashboardUiState.Failed((dashboardData as Result.Error).error)
        dashboardData is Result.Success -> DashboardUiState.Loaded((dashboardData as Result.Success<DashboardModel>).data)
        else -> DashboardUiState.Failed("Unknown error")
    }

    Contentment(
        minShowTimeMillis = 700L,
        delayMillis = 500L
    ) {
        when (uiState) {
            is DashboardUiState.Loading -> indicator {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Loaded -> content {
                DashboardScreenUI(uiState.data.data, navController)
            }
            is DashboardUiState.Failed -> content {
                val errorText = uiState.error
                if(errorText == "Unauthorized") {
                    navController.navigate(Route.LoginScreen) {
                        launchSingleTop = true
                    }
                }
                Text(
                    text = errorText ?: "Unknown error",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }

            else -> {

            }
        }
    }
}

@Composable
fun DashboardScreenUI(data: Data, navController: NavController) {
    Constants.CURRENT_BOTTOM_BAR_PAGE_ID = rememberSelectedIndex()
    val buttons = getBottomNavButtons(Constants.CURRENT_BOTTOM_BAR_PAGE_ID, navController)
    val rekomendViewModel: RekomenViewModel = hiltViewModel()
    val foodRecommendations by rekomendViewModel.foodRecommendations.observeAsState(emptyList())
    val exerciseRecommendations = rekomendViewModel.exerciseRecommendations.observeAsState()
    val isLoading by rekomendViewModel.isLoading.observeAsState(false)
    val error by rekomendViewModel.error.observeAsState(null)

    // Fetch recommendations when Dashboard loads
    LaunchedEffect(Unit) { rekomendViewModel.fetchRekomend() }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(buttons = buttons, navController = navController, currentScreen = Route.DashboardScreen)
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = { navController.navigate(Route.ChatScreenBot) }, // Ganti dengan route tujuan
            ) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.comment), // Ganti dengan icon yang kamu punya
                    contentDescription = "Tambah Rekomendasi",
                    modifier = Modifier
                    .width(24.dp)
                    .height(24.dp),
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { safeDrawingPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = safeDrawingPadding.calculateTopPadding(),
                    start = 16.dp,
                    bottom = safeDrawingPadding.calculateBottomPadding(),
                    end = 16.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserHeader(
                name = data.user.name,
                isDiabetes = data.user.diabetes,
                diabetesType = data.user.diabetesType
            )

            DailyProgressCard(
                calories = data.dailyProgress.calories,
                carbs = data.dailyProgress.carbs,
                sugar = data.dailyProgress.sugar,
            )

            StatusCard(
                satisfaction = data.status.satisfaction,
                message = data.status.message
            )
            DashboardRekomendasiSection(
                foodRecommendations = foodRecommendations,
                exerciseRecommendations = exerciseRecommendations.value?.exerciseList ?: emptyList(),
                isLoading = isLoading,
                error = error,
                onLihatExecClick = {
                    navController.navigate(Route.ExerciseRekomenScreen) {
                        launchSingleTop = true
                    }
                },
                onLihatFoodClick = {
                    navController.navigate(Route.FoodRekomenScreen) {
                        launchSingleTop = true
                    }
                },
                onMiniCourseClick = {
                    navController.navigate(Route.MiniCourseScreen) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun DashboardRekomendasiSection(
    foodRecommendations: List<com.amikom.sweetlife.data.remote.dto.rekomen.FoodRecommendation>,
    exerciseRecommendations: List<com.amikom.sweetlife.data.remote.dto.rekomen.Exercise>,
    isLoading: Boolean,
    error: String?,
    onLihatExecClick: () -> Unit,
    onLihatFoodClick: () -> Unit,
    onMiniCourseClick: () -> Unit
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(16.dp)
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )

    } else if (error != null) {
        Text(text = error, color = Color.Red)
    } else {
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Rekomendasi Makanan", style = MaterialTheme.typography.titleLarge)
        foodRecommendations.take(3).forEach { food ->
            RekomendItemFood(food)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = "Lihat Semua",
                modifier = Modifier
                    .background(Color.Transparent)
                    .clickable { onLihatFoodClick()
                               Log.d("DashboardRekomendasiSection", "Lihat Semua Makanan Clicked")
                               },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_navigate_next_24_white),
                contentDescription = "Lihat Semua",
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Rekomendasi Latihan", style = MaterialTheme.typography.titleLarge)
        exerciseRecommendations.take(3).forEach { exercise ->
            RekomendItemExec(exercise)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Lihat Semua",
                modifier = Modifier
                    .background(Color.Transparent)
                    .clickable { onLihatExecClick() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_navigate_next_24_white),
                contentDescription = "Lihat Semua",
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Mini Course For You", style = MaterialTheme.typography.titleLarge)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMiniCourseClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img_into_3), // Use your course icon
                    contentDescription = "Mini Course",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Mini Course",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Learn more about diabetes and healthy living.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// nama dan tipe diabetes
@Composable
private fun UserHeader(
    name: String,
    isDiabetes: Boolean,
    diabetesType: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Hi, $name!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if(isDiabetes) {
            Text(
                text = "Diabetes $diabetesType",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// daily progress
@Composable
private fun DailyProgressCard(
    calories: ProgressDetail,
    carbs: ProgressDetail,
    sugar: ProgressDetail,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🧋 Your Daily Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            ProgressItem(
                title = "🔥 Calorie",
                current = ceil(calories.current).toInt(),
                target = ceil(calories.target).toInt(),
                percentage = ceil(calories.percent).toInt(),
                satisfaction = calories.satisfaction
            )
            ProgressItem(
                title = "🍚 Carb",
                current = ceil(carbs.current).toInt(),
                target = ceil(carbs.target).toInt(),
                percentage = ceil(carbs.percent).toInt(),
                satisfaction = carbs.satisfaction
            )
            ProgressItem(
                title = "🧋 Sugar",
                current = ceil(sugar.current).toInt(),
                target = ceil(sugar.target).toInt(),
                percentage = ceil(sugar.percent).toInt(),
                satisfaction = sugar.satisfaction
            )
        }
    }
}

// progress bar
@Composable
private fun ProgressItem(
    title: String,
    current: Int,
    target: Int,
    percentage: Int,
    satisfaction: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val displayText = when (title) {
            "calories" -> "$current / $target kcal"
            "protein" -> "$current g dari $target g"
            "carbs" -> "$current g dari $target g"
            else -> "$current / $target"
        }

        Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth().height(16.dp),
        progress = percentage / 100f,
        //belom tau nilai satisfactionya apa
        color = when (satisfaction) {
            "PASS" -> Color.Green
            "OVER" -> Color.Red
            else -> Color.Yellow
        }
    )
}


// gambar laknat
@Composable
private fun StatusCard(
    satisfaction: String,
    message: String
) {
    Text(
        text = message,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().
        padding(16.dp, 16.dp, 16.dp, 0.dp),
        color = when(satisfaction) {
            "PASS" -> MaterialTheme.colorScheme.onBackground
            "UNDER" -> MaterialTheme.colorScheme.onBackground
            else -> Color.Red
        },
        style = MaterialTheme.typography.titleMedium
    )
}

//recomend
