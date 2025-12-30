package com.luminaapps.taigamobile.viewmodels

import com.luminaapps.taigamobile.domain.entities.*
import com.luminaapps.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import com.luminaapps.taigamobile.viewmodels.utils.testLazyPagingItems
import io.mockk.coVerify
import kotlin.test.Test

class ProjectSelectorViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: ProjectSelectorViewModel

    @BeforeTest
    fun setup() {
        viewModel = ProjectSelectorViewModel(mockAppComponent)
    }

    @Test
    fun `test list of projects`(): Unit = runBlocking {
        testLazyPagingItems(viewModel.projects, pageArg = { secondArg() }) {
            mockSearchRepository.searchProjects(any(), any())
        }
    }

    @Test
    fun `test select project`(): Unit = runBlocking {
        val mockProject = mockk<Project>(relaxed = true)
        viewModel.selectProject(mockProject)
        coVerify { mockSession.changeCurrentProject(any(), any()) }
    }
}