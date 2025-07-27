package com.amikom.sweetlife.domain.usecases.auth

import com.amikom.sweetlife.domain.manager.LocalAuthUserManager

class ReadUserId {
    private val localAuthUserManager: LocalAuthUserManager

    constructor(localAuthUserManager: LocalAuthUserManager) {
        this.localAuthUserManager = localAuthUserManager
    }

    suspend operator fun invoke(): String? {
        return localAuthUserManager.readUserId()
    }
}