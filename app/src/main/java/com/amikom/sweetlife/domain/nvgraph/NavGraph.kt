package com.amikom.sweetlife.domain.nvgraph

import SettingsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amikom.sweetlife.domain.manager.SessionViewModel
import com.amikom.sweetlife.ui.presentation.onboarding.OnBoardingScreen
import com.amikom.sweetlife.ui.presentation.onboarding.OnBoardingViewModel
import com.amikom.sweetlife.ui.screen.auth.forgot_password.CheckEmailScreen
import com.amikom.sweetlife.ui.screen.dashboard.DashboardScreen
import com.amikom.sweetlife.ui.screen.dashboard.DashboardViewModel
import com.amikom.sweetlife.ui.screen.auth.forgot_password.ForgotPasswordScreen
import com.amikom.sweetlife.ui.screen.auth.forgot_password.ForgotPasswordViewModel
import com.amikom.sweetlife.ui.screen.auth.login.LoginScreen
import com.amikom.sweetlife.ui.screen.auth.login.LoginViewModel
import com.amikom.sweetlife.ui.screen.auth.signup.SignUpViewModel
import com.amikom.sweetlife.ui.screen.auth.signup.SignupScreen
import com.amikom.sweetlife.ui.screen.home.HomeScreen
import com.amikom.sweetlife.ui.screen.profile.ProfileViewModel
import com.amikom.sweetlife.ui.screen.profile.UserProfile
import com.amikom.sweetlife.ui.screen.profile.UserProfileScreen
import com.amikom.sweetlife.ui.screen.profile.settings.SettingsScreen

@Composable
fun NavGraph(
    startDestination: Any
) {
    val navController = rememberNavController()

    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isUserLoggedOut by sessionViewModel.isUserLoggedOut.collectAsState()

    // Pantau logout secara global
    LaunchedEffect(isUserLoggedOut) {
        if (isUserLoggedOut) {
            navController.navigate(Route.LoginScreen) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable<Route.OnboardingScreen> {
            val viewModel: OnBoardingViewModel = hiltViewModel()
            OnBoardingScreen(event = viewModel::onEvent, navController = navController)
        }

        composable<Route.HomeScreen> {
            HomeScreen()
        }

        composable<Route.LoginScreen> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(event = loginViewModel::onEvent, navController = navController)
        }

        composable<Route.SignUpScreen> {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignupScreen(event = signUpViewModel::onEvent, navController = navController)
        }

        composable<Route.ForgotPasswordScreen> {
            val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(event = forgotPasswordViewModel::onEvent, navController = navController)
        }

        composable<Route.CheckEmailScreen> {
            CheckEmailScreen(navController = navController)
        }

        composable<Route.DashboardScreen> {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(viewModel = dashboardViewModel, navController = navController)
        }

        composable<Route.ProfileScreen> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            UserProfileScreen(
                profileViewModel = profileViewModel,
                navController = navController,
            )
        }

        composable<Route.SettingsScreen> {
            SettingsScreen()
        }
    }
}