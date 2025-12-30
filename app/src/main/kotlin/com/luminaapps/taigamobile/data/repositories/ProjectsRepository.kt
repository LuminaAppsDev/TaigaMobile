package com.luminaapps.taigamobile.data.repositories

import com.luminaapps.taigamobile.data.api.TaigaApi
import com.luminaapps.taigamobile.domain.paging.CommonPagingSource
import com.luminaapps.taigamobile.domain.repositories.IProjectsRepository
import com.luminaapps.taigamobile.state.Session
import javax.inject.Inject

class ProjectsRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IProjectsRepository {

    override suspend fun searchProjects(query: String, page: Int) = withIO {
        handle404 {
            taigaApi.getProjects(
                query = query,
                page = page,
                pageSize = CommonPagingSource.PAGE_SIZE
            )
        }
    }

    override suspend fun getMyProjects() = withIO {
        taigaApi.getProjects(memberId = session.currentUserId.value)
    }

    override suspend fun getUserProjects(userId: Long) = withIO {
        taigaApi.getProjects(memberId = userId)
    }
}