package com.luminaapps.taigamobile.ui.screens.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.R
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.CommonTask
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.domain.entities.Sprint
import com.luminaapps.taigamobile.domain.entities.Status
import com.luminaapps.taigamobile.domain.repositories.ISprintsRepository
import com.luminaapps.taigamobile.domain.repositories.ITasksRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.state.postUpdate
import com.luminaapps.taigamobile.ui.utils.MutableResultFlow
import com.luminaapps.taigamobile.ui.utils.NothingResult
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class SprintViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository
    @Inject lateinit var session: Session

    private var sprintId: Long = -1

    val sprint = MutableResultFlow<Sprint>()
    val statuses = MutableResultFlow<List<Status>>()
    val storiesWithTasks = MutableResultFlow<Map<CommonTask, List<CommonTask>>>()
    val storylessTasks = MutableResultFlow<List<CommonTask>>()
    val issues = MutableResultFlow<List<CommonTask>>()

    private var shouldReload = true

    init {
        appComponent.inject(this)
    }

    fun onOpen(sprintId: Long) {
        if (!shouldReload) return
        this.sprintId = sprintId
        loadData(isReloading = false)
        shouldReload = false
    }

    private fun loadData(isReloading: Boolean = true) = viewModelScope.launch {
        sprint.loadOrError(showLoading = !isReloading) {
            sprintsRepository.getSprint(sprintId).also {
                joinAll(
                    launch {
                        statuses.loadOrError(showLoading = false) { tasksRepository.getStatuses(CommonTaskType.Task) }
                    },
                    launch {
                        storiesWithTasks.loadOrError(showLoading = false) {
                            coroutineScope {
                                sprintsRepository.getSprintUserStories(sprintId)
                                    .map { it to async { tasksRepository.getUserStoryTasks(it.id) } }
                                    .associate { (story, tasks) -> story to tasks.await() }
                            }
                        }
                    },
                    launch {
                        issues.loadOrError(showLoading = false) { sprintsRepository.getSprintIssues(sprintId) }
                    },
                    launch {
                        storylessTasks.loadOrError(showLoading = false) { sprintsRepository.getSprintTasks(sprintId) }
                    }
                )
            }
        }
    }

    val editResult = MutableResultFlow<Unit>()
    fun editSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        editResult.loadOrError(R.string.permission_error) {
            sprintsRepository.editSprint(sprintId, name, start, end)
            session.sprintEdit.postUpdate()
            loadData().join()
        }
    }

    val deleteResult = MutableResultFlow<Unit>()
    fun deleteSprint() = viewModelScope.launch {
        deleteResult.loadOrError(R.string.permission_error) {
            sprintsRepository.deleteSprint(sprintId)
            session.sprintEdit.postUpdate()
        }
    }

    init {
        session.taskEdit.onEach {
            sprintId = -1
            sprint.value = NothingResult()
            statuses.value = NothingResult()
            storiesWithTasks.value = NothingResult()
            storylessTasks.value = NothingResult()
            issues.value = NothingResult()
            shouldReload = true
        }.launchIn(viewModelScope)
    }
}
