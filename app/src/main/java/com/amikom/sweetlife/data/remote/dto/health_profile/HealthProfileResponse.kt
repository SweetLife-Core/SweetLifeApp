package com.amikom.sweetlife.data.remote.dto.health_profile

import com.google.gson.annotations.SerializedName

data class HealthProfileResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Data(

	@field:SerializedName("activity_level")
	val activityLevel: String? = null,

	@field:SerializedName("smoking_history")
	val smokingHistory: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("diabetes_prediction")
	val diabetesPrediction: DiabetesPrediction? = null,

	@field:SerializedName("weight")
	val weight: Double? = null,

	@field:SerializedName("is_diabetic")
	val isDiabetic: Boolean? = null,

	@field:SerializedName("diabetic_type")
	val diabeticType: String? = null,

	@field:SerializedName("insulin_level")
	val insulinLevel: Int? = null,

	@field:SerializedName("blood_pressure")
	val bloodPressure: Int? = null,

	@field:SerializedName("has_heart_disease")
	val hasHeartDisease: Boolean? = null,

	@field:SerializedName("height")
	val height: Double? = null,

	@field:SerializedName("diabetes")
	val diabetes: String = ""
)

data class DiabetesPrediction(

	@field:SerializedName("risk_level")
	val riskLevel: String? = null,

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("risk_percentage")
	val riskPercentage: Double? = null
)

