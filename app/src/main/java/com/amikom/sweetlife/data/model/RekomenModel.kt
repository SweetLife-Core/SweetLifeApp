package com.amikom.sweetlife.data.model

import com.google.gson.annotations.SerializedName

data class RekomenModel(
    val data: RekomenData? = null,
    val status: Boolean = false
)

data class RekomenData(
    @SerializedName("exercise_recommendations")
    val exerciseRecommendations: ExerciseRecommendations? = null,
    @SerializedName("food_recommendation")
    val foodRecommendation: List<FoodRecommendation>? = emptyList()
)

data class ExerciseRecommendations(
    @SerializedName("calories_burned")
    val caloriesBurned: Int = 0,
    @SerializedName("exercise_duration")
    val exerciseDuration: Double = 0.0,
    @SerializedName("exercise_list")
    val exerciseList: List<Exercise>? = emptyList()
)

data class Exercise(
    val image: String = "",
    val name: String = "",
    val desc: String = ""
)

data class FoodRecommendation(
    val name: String = "",
    val image: String = "",
    val details: FoodDetails? = null
)

data class FoodDetails(
    val calories: String = "",
    val carbohydrate: String = "",
    val fat: String = "",
    val proteins: String = ""
)

