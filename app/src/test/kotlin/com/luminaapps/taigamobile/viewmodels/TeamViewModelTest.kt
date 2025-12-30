package com.luminaapps.taigamobile.viewmodels

import com.luminaapps.taigamobile.domain.entities.TeamMember
import com.luminaapps.taigamobile.ui.screens.team.TeamViewModel
import com.luminaapps.taigamobile.ui.utils.ErrorResult
import com.luminaapps.taigamobile.ui.utils.SuccessResult
import com.luminaapps.taigamobile.viewmodels.utils.assertResultEquals
import com.luminaapps.taigamobile.viewmodels.utils.notFoundException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs


class TeamViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: TeamViewModel

    @BeforeTest
    fun setup() {
        viewModel = TeamViewModel(mockAppComponent)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val listTeamMember = mockk<List<TeamMember>>(relaxed = true)

        coEvery { mockUsersRepository.getTeam() } returns listTeamMember
        viewModel.onOpen()

        assertResultEquals(SuccessResult(listTeamMember), viewModel.team.value)

    }

    @Test
    fun `test on open error`(): Unit = runBlocking {
        coEvery { mockUsersRepository.getTeam() } throws notFoundException
        viewModel.onOpen()

        assertIs<ErrorResult<List<TeamMember>>>(viewModel.team.value)

    }
}
