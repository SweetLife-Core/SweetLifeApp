package com.amikom.sweetlife.domain.nvgraph

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.amikom.sweetlife.ui.screen.profile.settings.SettingsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.amikom.sweetlife.domain.manager.SessionViewModel
import com.amikom.sweetlife.domain.nvgraph.Route.ChatScreenBot
import com.amikom.sweetlife.domain.nvgraph.Route.FoodRekomenScreen
import com.amikom.sweetlife.ui.presentation.onboarding.OnBoardingScreen
import com.amikom.sweetlife.ui.presentation.onboarding.OnBoardingViewModel
import com.amikom.sweetlife.ui.screen.Chatbot.ChatBotScreen
import com.amikom.sweetlife.ui.screen.Chatbot.ChatBotViewModel
import com.amikom.sweetlife.ui.screen.History.HistoryScreen
import com.amikom.sweetlife.ui.screen.History.HistoryViewModel
import com.amikom.sweetlife.ui.screen.assesment.AssessmentScreen
import com.amikom.sweetlife.ui.screen.assesment.AssessmentViewModel
import com.amikom.sweetlife.ui.screen.auth.forgot_password.CheckEmailScreen
import com.amikom.sweetlife.ui.screen.dashboard.DashboardScreen
import com.amikom.sweetlife.ui.screen.dashboard.DashboardViewModel
import com.amikom.sweetlife.ui.screen.auth.forgot_password.ForgotPasswordScreen
import com.amikom.sweetlife.ui.screen.auth.forgot_password.ForgotPasswordViewModel
import com.amikom.sweetlife.ui.screen.auth.login.LoginScreen
import com.amikom.sweetlife.ui.screen.auth.login.LoginViewModel
import com.amikom.sweetlife.ui.screen.auth.signup.SignUpViewModel
import com.amikom.sweetlife.ui.screen.auth.signup.SignupScreen
import com.amikom.sweetlife.ui.screen.grocery.GroceryScreen
import com.amikom.sweetlife.ui.screen.home.HomeScreen
import com.amikom.sweetlife.ui.screen.minicourse.MiniCourseScreen
import com.amikom.sweetlife.ui.screen.profile.EditHealthData.EditDataScreen
import com.amikom.sweetlife.ui.screen.profile.EditHealthData.EditDataScreenViewModel
import com.amikom.sweetlife.ui.screen.profile.ProfileViewModel
import com.amikom.sweetlife.ui.screen.profile.UserProfileScreen
import com.amikom.sweetlife.ui.screen.profile.editProfile.EditProfileScreen
import com.amikom.sweetlife.ui.screen.profile.editProfile.EditProfileViewModel
import com.amikom.sweetlife.ui.screen.profile.settings.SettingsScreen
import com.amikom.sweetlife.ui.screen.rekomend.ExerciseRekomenScreen
import com.amikom.sweetlife.ui.screen.rekomend.FoodRekomenScreen
import com.amikom.sweetlife.ui.screen.rekomend.RekomenScreen
import com.amikom.sweetlife.ui.screen.rekomend.RekomenViewModel
import com.amikom.sweetlife.ui.screen.scan.CameraScanViewModel
import com.amikom.sweetlife.ui.screen.scan.CameraScreen
import com.amikom.sweetlife.ui.screen.scan.ResultAndAdditionalScreen
import com.amikom.sweetlife.ui.screen.scan.ResultScanViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    startDestination: Any
) {
    val navController = rememberNavController()

    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isUserLoggedOut by sessionViewModel.isUserLoggedOut.collectAsState()

    // Pantau logout secara global
    if(startDestination !== Route.OnboardingScreen) {
        LaunchedEffect(isUserLoggedOut) {
            if (isUserLoggedOut) {
                navController.navigate(Route.LoginScreen) {
                    popUpTo(0) { inclusive = true }
                }
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

        composable<Route.MiniCourseScreen>{
            MiniCourseScreen()
        }

        composable<Route.GroceryScreen> {
            GroceryScreen()
        }

        composable<Route.AssessmentScreen> {
            val assessmentViewModel: AssessmentViewModel = hiltViewModel()
            AssessmentScreen(navController = navController, viewModel = assessmentViewModel)
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

        composable<Route.EditProfileScreen> {
            val EditProfileViewModel: EditProfileViewModel = hiltViewModel()
            EditProfileScreen(
                viewModel = EditProfileViewModel,
                onEditProfile = {},
                navController = navController
            )
        }

        composable<Route.ProfileScreen> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            UserProfileScreen(
                profileViewModel = profileViewModel,
                navController = navController,
            )
        }

        composable<Route.HistoryScreen> {
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(viewModel = viewModel, navController = navController)
        }

        composable<Route.SettingsScreen> {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(settingsViewModel)
        }

        composable<Route.EditHealthScreen> {
            val viewModel: EditDataScreenViewModel = hiltViewModel()
            EditDataScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable<Route.ExerciseRekomenScreen> {
            val viewModel: RekomenViewModel = hiltViewModel()
            ExerciseRekomenScreen(viewModel = viewModel, navController = navController)
        }

        composable<Route.ChatScreenBot> {
            val viewModel: ChatBotViewModel = hiltViewModel()
            val userId by sessionViewModel.userId.collectAsState()
            ChatBotScreen(viewModel = viewModel)
        }

        composable<Route.FoodRekomenScreen> {
            val viewModel: RekomenViewModel = hiltViewModel()
            FoodRekomenScreen(viewModel = viewModel, navController = navController)
        }

        composable<Route.CameraScreen> {
            val viewModel: CameraScanViewModel = hiltViewModel()
            CameraScreen(viewModel = viewModel, onBackPressed = { navController.popBackStack() }, navController = navController)
        }

        composable<Route.ResultScanScreen> {
            val args = it.toRoute<Route.ResultScanScreen>()
            val viewModel: ResultScanViewModel = hiltViewModel()

            ResultAndAdditionalScreen(args.listFood, viewModel, navController)
        }
    }
}