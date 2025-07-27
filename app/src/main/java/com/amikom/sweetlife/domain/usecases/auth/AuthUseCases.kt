package com.amikom.sweetlife.domain.usecases.auth

data class AuthUseCases(
    val logout: LogoutAction,
    val login: LoginAction,
    val checkIsUserLogin: CheckIsUserLogin,
    val readUserAllToken: ReadUserAllToken,
    val saveUserInfoLogin: SaveUserInfoLogin,

    val checkHasHealthProfile: CheckHasHealthProfile,
    val saveHealthProfile: SaveHealthProfile,

    val register: RegisterAction,

    val forgotPassword: ForgotUserPassword,

    val refreshNewToken: RefreshNewTokenAction,
    val saveNewToken: SaveNewToken,

    val readUserId: ReadUserId,
)