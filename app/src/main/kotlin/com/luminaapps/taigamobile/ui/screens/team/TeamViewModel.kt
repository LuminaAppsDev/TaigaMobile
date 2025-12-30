package com.luminaapps.taigamobile.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.TeamMember
import com.luminaapps.taigamobile.domain.repositories.IUsersRepository
import com.luminaapps.taigamobile.ui.utils.MutableResultFlow
import com.luminaapps.taigamobile.ui.utils.NothingResult
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeamViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var session: Session

    val projectName by lazy { session.currentProjectName }
    val team = MutableResultFlow<List<TeamMember>?>()

    private var shouldReload = true

    init {
        appComponent.inject(this)
    }

    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            team.loadOrError { usersRepository.getTeam() }
        }
        shouldReload = false
    }

    init {
        session.currentProjectId.onEach {
            team.value = NothingResult()
            shouldReload = true
        }.launchIn(viewModelScope)
    }
}
