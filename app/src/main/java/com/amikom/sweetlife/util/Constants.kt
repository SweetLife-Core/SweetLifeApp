package com.amikom.sweetlife.util

import androidx.compose.runtime.MutableState

object Constants {
    const val USER_SETTINGS = "userSettings"

    const val APP_ENTRY = "appEntry"

    const val LOCAL_USER_INFO = "localUserInfo"

    const val API_VERSION = "/api/v1/"

    const val USER_EMAIL = "userEmail"
    const val USER_NAME = "userName"
    const val USER_GENDER = "userGender"
    const val USER_TOKEN = "userToken"
    const val USER_REFRESH_TOKEN = "userRefreshToken"
    const val USER_IS_LOGIN = "userIsLogin"

    var CURRENT_TOKEN: String = ""

    lateinit var CURRENT_BOTTOM_BAR_PAGE_ID: MutableState<Int>
}