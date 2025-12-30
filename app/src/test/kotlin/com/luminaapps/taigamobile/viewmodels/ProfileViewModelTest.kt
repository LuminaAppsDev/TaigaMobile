package com.luminaapps.taigamobile.viewmodels

import com.luminaapps.taigamobile.domain.entities.Project
import com.luminaapps.taigamobile.domain.entities.Stats
import com.luminaapps.taigamobile.domain.entities.User
import com.luminaapps.taigamobile.ui.screens.profile.ProfileViewModel
import com.luminaapps.taigamobile.ui.utils.ErrorResult
import com.luminaapps.taigamobile.ui.utils.SuccessResult
import com.luminaapps.taigamobile.viewmodels.utils.accessDeniedException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class ProfileViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: ProfileViewModel

    @BeforeTest
    fun setup() {
        viewModel = ProfileViewModel(mockAppComponent)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val userId = 0L
        val mockUser = mockk<User>(relaxed = true)
        val mockUserStats = mockk<Stats>(relaxed = true)
        val mockUserProjects = mockk<List<Project>>(relaxed = true)

        coEvery { mockUsersRepository.getUser(any()) } returns mockUser
        coEvery { mockUsersRepository.getUserStats(any()) } returns mockUserStats
        coEvery { mockSearchRepository.getUserProjects(any()) } returns mockUserProjects

        viewModel.onOpen(userId)
        assertIs<SuccessResult<User>>(viewModel.currentUser.value)
        assertIs<SuccessResult<Stats>>(viewModel.currentUserStats.value)
        assertIs<SuccessResult<List<Project>>>(viewModel.currentUserProjects.value)
    }

    @Test
    fun `test on open error`(): Unit = runBlocking {
        val userId = 0L

        coEvery { mockUsersRepository.getUser(any()) } throws accessDeniedException
        coEvery { mockUsersRepository.getUserStats(any()) } throws accessDeniedException
        coEvery { mockSearchRepository.getUserProjects(any()) } throws accessDeniedException

        viewModel.onOpen(userId)
        assertIs<ErrorResult<User>>(viewModel.currentUser.value)
        assertIs<ErrorResult<Stats>>(viewModel.currentUserStats.value)
        assertIs<ErrorResult<List<Project>>>(viewModel.currentUserProjects.value)
    }
}