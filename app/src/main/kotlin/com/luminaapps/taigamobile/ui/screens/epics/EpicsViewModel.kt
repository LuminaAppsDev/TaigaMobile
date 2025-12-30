package com.luminaapps.taigamobile.ui.screens.epics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.luminaapps.taigamobile.TaigaApp
import com.luminaapps.taigamobile.dagger.AppComponent
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.domain.entities.FiltersData
import com.luminaapps.taigamobile.domain.paging.CommonPagingSource
import com.luminaapps.taigamobile.domain.repositories.ITasksRepository
import com.luminaapps.taigamobile.state.Session
import com.luminaapps.taigamobile.ui.utils.MutableResultFlow
import com.luminaapps.taigamobile.ui.utils.asLazyPagingItems
import com.luminaapps.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpicsViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName by lazy { session.currentProjectName }

    private var shouldReload = true

    init {
        appComponent.inject(this)
    }

    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            filters.loadOrError { tasksRepository.getFiltersData(CommonTaskType.Epic) }
            filters.value.data?.let {
                session.changeEpicsFilters(activeFilters.value.updateData(it))
            }
        }
        shouldReload = false
    }

    val filters = MutableResultFlow<FiltersData>()
    val activeFilters by lazy { session.epicsFilters }
    @OptIn(ExperimentalCoroutinesApi::class)
    val epics by lazy {
        activeFilters.flatMapLatest { filters ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE, enablePlaceholders = false)) {
                CommonPagingSource { tasksRepository.getEpics(it, filters) }
            }.flow
        }.asLazyPagingItems(viewModelScope)
    }

    fun selectFilters(filters: FiltersData) {
        session.changeEpicsFilters(filters)
    }

    init {
        session.currentProjectId.onEach {
            epics.refresh()
            shouldReload = true
        }.launchIn(viewModelScope)

        session.taskEdit.onEach {
            epics.refresh()
        }.launchIn(viewModelScope)
    }
}
