package com.amikom.sweetlife

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.amikom.sweetlife.domain.nvgraph.NavGraph
import com.amikom.sweetlife.domain.nvgraph.Route
import com.amikom.sweetlife.ui.theme.SweetLifeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashCondition
            }
        }

        enableEdgeToEdge()

//        val isDarkMode = viewModel.isDarkMode.value
//        val isDarkMode = remember { mutableStateOf(false) }
//        viewModel.isDarkMode.observeForever{
//            isDarkMode = it
//            Log.d("BIJIX_THEME", it.toString())
//        }
        var isDarkMode = false

        viewModel.isDarkMode.observeForever { isDark ->
            isDarkMode = isDark
            Log.d("BIJIX_THEME", "Theme updated to: $isDark")

            // Render UI dengan tema baru
            setContent {
                SweetLifeTheme(darkTheme = isDarkMode) {
                    Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                        val startDestination = viewModel.startDestination
                        NavGraph(startDestination = startDestination)
                    }
                }
            }
        }

//        setContent {
//            SweetLifeTheme(darkTheme = isDarkMode) {

//                Jangan dipake deprecated anjer
//                val isSystemInDarkMode = isSystemInDarkTheme()
//                val systemController = rememberSystemUiController()
//
//                SideEffect {
//                    systemController.setSystemBarsColor(
//                        color = Color.Transparent,
//                        darkIcons = !isSystemInDarkMode
//                    )
//                }

//                Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
//                    val startDestination = viewModel.startDestination
//                    NavGraph(startDestination = startDestination)
//                }
//            }
//        }
    }
}