package com.amikom.sweetlife.data.model

data class UserModel(
    val id: String,
    val email: String,
    val name: String,
    val gender: String,
    val token: String,
    val refreshToken: String,
    val isLogin: Boolean,
    val hasHealthProfile: Boolean
)
