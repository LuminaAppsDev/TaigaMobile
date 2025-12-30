package com.luminaapps.taigamobile.domain.repositories

import com.luminaapps.taigamobile.domain.entities.AuthType

interface IAuthRepository {
    suspend fun auth(taigaServer: String, authType: AuthType, password: String, username: String)
}