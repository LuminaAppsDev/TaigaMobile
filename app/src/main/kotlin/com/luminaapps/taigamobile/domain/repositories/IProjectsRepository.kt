package com.luminaapps.taigamobile.domain.repositories

import com.luminaapps.taigamobile.domain.entities.Project

interface IProjectsRepository {
    suspend fun searchProjects(query: String, page: Int): List<Project>
    suspend fun getMyProjects(): List<Project>
    suspend fun getUserProjects(userId: Long): List<Project>
}