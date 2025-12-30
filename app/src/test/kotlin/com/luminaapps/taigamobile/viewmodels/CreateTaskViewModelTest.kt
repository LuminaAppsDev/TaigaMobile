package com.luminaapps.taigamobile.viewmodels

import com.luminaapps.taigamobile.domain.entities.CommonTask
import com.luminaapps.taigamobile.domain.entities.CommonTaskType
import com.luminaapps.taigamobile.ui.screens.createtask.CreateTaskViewModel
import com.luminaapps.taigamobile.ui.utils.ErrorResult
import com.luminaapps.taigamobile.ui.utils.SuccessResult
import com.luminaapps.taigamobile.viewmodels.utils.accessDeniedException
import com.luminaapps.taigamobile.viewmodels.utils.assertResultEquals
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class CreateTaskViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: CreateTaskViewModel

    @BeforeTest
    fun setup() = runBlocking {
        viewModel = CreateTaskViewModel(mockAppComponent)
    }

    @Test
    fun `test create task`(): Unit = runBlocking {
        val mockCommonTaskType = mockk<CommonTaskType>(relaxed = true)
        val mockCommonTask = mockk<CommonTask>(relaxed = true)
        val title = "title"
        val description = "description"

        coEvery {
            mockTaskRepository.createCommonTask(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns mockCommonTask
        viewModel.createTask(mockCommonTaskType, title, description)
        assertResultEquals(SuccessResult(mockCommonTask), viewModel.creationResult.value)

        coEvery {
            mockTaskRepository.createCommonTask(
                any(),
                neq(title),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.createTask(mockCommonTaskType, title + "error", description)
        assertIs<ErrorResult<CommonTask>>(viewModel.creationResult.value)
    }
}