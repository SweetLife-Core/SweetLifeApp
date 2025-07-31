package com.amikom.sweetlife.ui.presentation.onboarding

import androidx.annotation.DrawableRes
import com.amikom.sweetlife.R

sealed class OnboardingModel(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
) {
    data object FirstPages : OnboardingModel(
        image = R.drawable.welco1,
        title = "Welcome to sweetLife.",
        description = "Start your health journey today! sweetLife helps you monitor your habits and achieve the healthy lifestyle you've always dreamed of."
    )
    data object SecondPages : OnboardingModel(
        image = R.drawable.welco2,
        title = "Safe and reliable to use.",
        description = "Your health data is our priority. With sweetLife, your information is protected and reliable for the best nutritional guidance."
    )
    data object ThirdPages : OnboardingModel(
        image = R.drawable.welco3,
        title = "Can scan various types\nof food.",
        description = "Discover the nutritional content of any food with ease. Scan labels or ingredients for smarter, healthier diet choices."
    )
}