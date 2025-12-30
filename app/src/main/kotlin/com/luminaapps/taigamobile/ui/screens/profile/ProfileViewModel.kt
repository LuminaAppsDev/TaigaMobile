package com.luminaapps.taigamobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.Project
import com.luminaapps.taigamobile.domain.entities.Stats
import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.domain.repositories.IProjectsRepository
import com.luminaapps.taigamobile.domain.repositories.IUsersRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.ui.utils.MutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject
    lateinit var usersRepository: IUsersRepository

    @Inject
    lateinit var projectsRepository: IProjectsRepository

    @Inject
    lateinit var session: Session

    val currentUser = MutableResultFlow<User>()
    val currentUserStats = MutableResultFlow<Stats>()
    val currentUserProjects = MutableResultFlow<List<Project>>()
    val currentProjectId by lazy { session.currentProjectId }

    init {
        appComponent.inject(this)
    }

    fun onOpen(userId: Long) = viewModelScope.launch {
        currentUser.loadOrError { usersRepository.getUser(userId) }
        currentUserStats.loadOrError { usersRepository.getUserStats(userId) }
        currentUserProjects.loadOrError { projectsRepository.getUserProjects(userId) }
    }
}