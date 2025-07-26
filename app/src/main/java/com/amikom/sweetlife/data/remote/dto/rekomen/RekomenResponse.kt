package com.amikom.sweetlife.data.remote.dto.rekomen

import com.google.gson.annotations.SerializedName

data class RekomenResponse(
    @SerializedName("data") val data: Data? = null,
    @SerializedName("status") val status: Boolean = false
)


data class Data(
    @SerializedName("food_recommendation")
    val foodRecommendation: List<FoodRecommendation> = emptyList(),

    @SerializedName("exercise_recommendations")
    val exerciseRecommendations: ExerciseRecommendations? = null
)

data class FoodRecommendation(
    @SerializedName("name") val name: String = "",
    @SerializedName("details") val details: FoodDetails? = null,
    @SerializedName("image") val image: String = ""
)

data class FoodDetails(
    @SerializedName("calories") val calories: String = "",
    @SerializedName("carbohydrate") val carbohydrate: String = "",
    @SerializedName("fat") val fat: String = "",
    @SerializedName("proteins") val proteins: String = ""
)

data class ExerciseRecommendations(
    @SerializedName("calories_burned") val caloriesBurned: Int = 0,
    @SerializedName("exercise_duration") val exerciseDuration: Double = 0.0,
    @SerializedName("exercise_list") val exerciseList: List<Exercise> = emptyList()
)

data class Exercise(
    @SerializedName("name") val name: String = "",
    @SerializedName("desc") val desc: String = "",
    @SerializedName("image") val image: String = ""
)


