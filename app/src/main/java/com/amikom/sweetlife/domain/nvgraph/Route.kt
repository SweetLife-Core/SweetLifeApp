package com.amikom.sweetlife.domain.nvgraph

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object HomeScreen : Route()

    @Serializable
    data object OnboardingScreen : Route()

    @Serializable
    data object LoginScreen : Route()

    @Serializable
    data object SignUpScreen : Route()

    @Serializable
    data object ForgotPasswordScreen: Route()

    @Serializable
    data object CheckEmailScreen: Route()

    @Serializable
    data object DashboardScreen: Route()

    @Serializable
    data object ProfileScreen: Route()

    @Serializable
    data object HistoryScreen: Route()

    @Serializable
    data object SettingsScreen: Route()

    @Serializable
    data object EditProfileScreen: Route()

    @Serializable
    data object AssessmentScreen : Route()

    @Serializable
    data object EditHealthScreen : Route()

    @Serializable
    data object CameraScreen : Route()

    @Serializable
    data class ResultScanScreen(
        val listFood: String
    ) : Route()

    @Serializable
    data object FoodRekomenScreen: Route()

    @Serializable
    data object ExerciseRekomenScreen: Route()

    @Serializable
    data object ChatScreenBot: Route()

    @Serializable
    data object MiniCourseScreen: Route()

    @Serializable
    data object GroceryScreen: Route()
}