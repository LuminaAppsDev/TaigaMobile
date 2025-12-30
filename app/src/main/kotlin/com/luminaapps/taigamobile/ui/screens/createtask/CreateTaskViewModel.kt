package com.luminaapps.taigamobile.ui.screens.createtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.CommonTask
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.domain.repositories.ITasksRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.state.postUpdate
import com.luminaapps.taigamobile.ui.utils.mutableResultFlow
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateTaskViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var session: Session

    init {
        appComponent.inject(this)
    }

    val creationResult = mutableResultFlow<CommonTask>()

    fun createTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long? = null,
        sprintId: Long? = null,
        statusId: Long? = null,
        swimlaneId: Long? = null
    ) = viewModelScope.launch {
        creationResult.loadOrError(preserveValue = false) {
            tasksRepository.createCommonTask(commonTaskType, title, description, parentId, sprintId, statusId, swimlaneId).also {
                session.taskEdit.postUpdate()
            }
        }
    }
}