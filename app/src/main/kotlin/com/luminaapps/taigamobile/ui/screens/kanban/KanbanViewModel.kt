package com.luminaapps.taigamobile.ui.screens.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.CommonTaskExtended
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.domain.entities.Status
import com.luminaapps.taigamobile.domain.entities.Swimlane
import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.domain.repositories.ITasksRepository
import com.luminaapps.taigamobile.domain.repositories.IUsersRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.state.subscribeToAll
import com.luminaapps.taigamobile.ui.utils.mutableResultFlow
import com.luminaapps.taigamobile.ui.utils.NothingResult
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class KanbanViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var session: Session

    val projectName by lazy { session.currentProjectName }

    val statuses = mutableResultFlow<List<Status>>()
    val team = mutableResultFlow<List<User>>()
    val stories = mutableResultFlow<List<CommonTaskExtended>>()
    val swimlanes = mutableResultFlow<List<Swimlane?>>()

    val selectedSwimlane = MutableStateFlow<Swimlane?>(null)

    private var shouldReload = true

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        if (!shouldReload) return@launch
        joinAll(
            launch {
                statuses.loadOrError(preserveValue = false) { tasksRepository.getStatuses(CommonTaskType.UserStory) }
            },
            launch {
                team.loadOrError(preserveValue = false) { usersRepository.getTeam().map { it.toUser() } }
            },
            launch {
                stories.loadOrError(preserveValue = false) { tasksRepository.getAllUserStories() }
            },
            launch {
                swimlanes.loadOrError {
                    listOf(null) + tasksRepository.getSwimlanes() // prepend null to show "unclassified" swimlane
                }
            }
        )
        shouldReload = false
    }

    fun selectSwimlane(swimlane: Swimlane?) {
        selectedSwimlane.value = swimlane
    }

    init {
        viewModelScope.subscribeToAll(session.currentProjectId, session.taskEdit) {
            statuses.value = NothingResult()
            team.value = NothingResult()
            stories.value = NothingResult()
            swimlanes.value = NothingResult()
            shouldReload = true
        }
    }
}
