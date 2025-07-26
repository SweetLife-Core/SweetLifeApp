    package com.amikom.sweetlife.ui.screen.rekomend

    import android.util.Log
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.livedata.observeAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.res.painterResource
    import androidx.hilt.navigation.compose.hiltViewModel
    import androidx.navigation.NavController
    import com.amikom.sweetlife.R
    import com.amikom.sweetlife.domain.nvgraph.Route
    import com.amikom.sweetlife.ui.component.BottomNavigationBar
    import com.amikom.sweetlife.ui.component.getBottomNavButtons
    import com.amikom.sweetlife.util.Constants
    import me.rmyhal.contentment.Contentment

    sealed class ExerciseRekomenUiState {
        object Loading : ExerciseRekomenUiState()
        data class Loaded(val exerciseList: List<com.amikom.sweetlife.data.remote.dto.rekomen.Exercise>) : ExerciseRekomenUiState()
        data class Failed(val error: String?) : ExerciseRekomenUiState()
    }

    @Composable
    fun ExerciseRekomenScreen(
        viewModel: RekomenViewModel = hiltViewModel(),
     navController: NavController
    ) {
        LaunchedEffect(Unit) {
            viewModel.fetchRekomend()
            Log.d("ExerciseRekomenScreen", "Fetching exercise recommendations")
        }

        val exerciseRecommendations = viewModel.exerciseRecommendations.observeAsState()
        val isLoading by viewModel.isLoading.observeAsState(false)
        val error by viewModel.error.observeAsState(null)
        val exerciseList = exerciseRecommendations.value?.exerciseList ?: emptyList()
        val selectedIndex = Constants.CURRENT_BOTTOM_BAR_PAGE_ID
        val buttons = getBottomNavButtons(selectedIndex, navController)

        val uiState = when {
            isLoading -> ExerciseRekomenUiState.Loading
            error != null -> ExerciseRekomenUiState.Failed(error)
            else -> ExerciseRekomenUiState.Loaded(exerciseList)
        }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    buttons = buttons,
                    navController = navController,
                    currentScreen = Route.ExerciseRekomenScreen
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (uiState) {
                    is ExerciseRekomenUiState.Loading ->
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    is ExerciseRekomenUiState.Failed ->
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = uiState.error ?: "Unknown error", style = MaterialTheme.typography.bodyLarge)
                        }
                    is ExerciseRekomenUiState.Loaded ->
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.exerciseList) { exercise ->
                                RekomendItemExec(exercise)
                            }
                        }
                }
            }
        }
    }
