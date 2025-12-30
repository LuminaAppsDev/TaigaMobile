package com.luminaapps.taigamobile.domain.repositories

import com.luminaapps.taigamobile.domain.entities.Stats
import com.luminaapps.taigamobile.domain.entities.TeamMember
import com.luminaapps.taigamobile.domain.entities.User

interface IUsersRepository {
    suspend fun getMe(): User
    suspend fun getUser(userId: Long): User
    suspend fun getUserStats(userId: Long): Stats
    suspend fun getTeam(): List<TeamMember>
}